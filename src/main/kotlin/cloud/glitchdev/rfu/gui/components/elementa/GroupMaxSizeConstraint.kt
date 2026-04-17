package cloud.glitchdev.rfu.gui.components.elementa

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.SizeConstraint
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.dsl.pixel
import java.lang.ref.WeakReference

class GroupMaxSizeConstraint(
    val groupKey: String,
    val baseConstraint: SizeConstraint = 0.pixel
) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate: Boolean
        get() = true
        set(value) {}
    override var constrainTo: UIComponent? = null

    private fun isDeepHidden(comp: UIComponent): Boolean {
        var current: UIComponent? = comp
        while (current != null) {
            if (current is Window) return false
            if (!current.hasParent) return true
            val parent = current.parent
            if (parent === current) return false
            if (!parent.children.contains(current)) return true
            current = parent
        }
        return true
    }

    private fun getMaxValue(component: UIComponent, type: ConstraintType): Float {
        val baseValue = when (type) {
            ConstraintType.WIDTH -> baseConstraint.getWidthImpl(component)
            ConstraintType.HEIGHT -> baseConstraint.getHeightImpl(component)
            ConstraintType.RADIUS -> baseConstraint.getRadiusImpl(component)
            else -> 0f
        }

        val window = Window.ofOrNull(component) ?: return baseValue
        val frameTime = window.animationTimeMs
        
        val groupMap = globalGroups.getOrPut(groupKey) { mutableMapOf() }
        val hidden = isDeepHidden(component)
        
        if (!hidden && !groupMap.containsKey(this)) {
            groupMap[this] = WeakReference(component)
            widthCaches[groupKey]?.lastFrameTime = -1
            heightCaches[groupKey]?.lastFrameTime = -1
            radiusCaches[groupKey]?.lastFrameTime = -1
        }

        val cacheMap = when (type) {
            ConstraintType.WIDTH -> widthCaches
            ConstraintType.HEIGHT -> heightCaches
            ConstraintType.RADIUS -> radiusCaches
            else -> return baseValue
        }
        
        val cache = cacheMap.getOrPut(groupKey) { FrameCache() }
        if (cache.lastFrameTime == frameTime) {
            return cache.value
        }

        val iterator = groupMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val comp = entry.value.get()
            if (comp == null || isDeepHidden(comp)) {
                iterator.remove()
            }
        }

        val max = if (groupMap.isEmpty()) {
            baseValue
        } else {
            groupMap.entries.mapNotNull { (constraint, weakRef) ->
                val comp = weakRef.get()
                if (comp != null) {
                    when (type) {
                        ConstraintType.WIDTH -> constraint.baseConstraint.getWidthImpl(comp)
                        ConstraintType.HEIGHT -> constraint.baseConstraint.getHeightImpl(comp)
                        ConstraintType.RADIUS -> constraint.baseConstraint.getRadiusImpl(comp)
                        else -> null
                    }
                } else null
            }.maxOrNull() ?: baseValue
        }

        cache.lastFrameTime = frameTime
        cache.value = max
        return max
    }

    override fun getWidthImpl(component: UIComponent): Float = getMaxValue(component, ConstraintType.WIDTH)
    override fun getHeightImpl(component: UIComponent): Float = getMaxValue(component, ConstraintType.HEIGHT)
    override fun getRadiusImpl(component: UIComponent): Float = getMaxValue(component, ConstraintType.RADIUS)

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        baseConstraint.visitImpl(visitor, type)
    }

    companion object {
        private val globalGroups = mutableMapOf<String, MutableMap<GroupMaxSizeConstraint, WeakReference<UIComponent>>>()
        
        private class FrameCache {
            var lastFrameTime: Long = -1
            var value: Float = 0f
        }

        private val widthCaches = mutableMapOf<String, FrameCache>()
        private val heightCaches = mutableMapOf<String, FrameCache>()
        private val radiusCaches = mutableMapOf<String, FrameCache>()

        fun clearGroup(groupKey: String) {
            globalGroups.remove(groupKey)
            widthCaches.remove(groupKey)
            heightCaches.remove(groupKey)
            radiusCaches.remove(groupKey)
        }

        fun clearAll() {
            globalGroups.clear()
            widthCaches.clear()
            heightCaches.clear()
            radiusCaches.clear()
        }
    }
}
