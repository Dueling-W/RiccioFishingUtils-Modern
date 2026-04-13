package cloud.glitchdev.rfu.gui.window

import cloud.glitchdev.rfu.RiccioFishingUtils.mc
import cloud.glitchdev.rfu.gui.UIScheme
import cloud.glitchdev.rfu.gui.components.UIPopup
import cloud.glitchdev.rfu.events.managers.PartyFinderEvents.registerPartyListChangedEvent
import cloud.glitchdev.rfu.events.managers.PartyFinderEvents.registerMyPartyChangedEvent
import cloud.glitchdev.rfu.events.managers.PartyFinderEvents.registerPartyCreatedEvent
import cloud.glitchdev.rfu.events.managers.ErrorEvents.registerErrorMessageEvent
import cloud.glitchdev.rfu.gui.components.UIButton
import cloud.glitchdev.rfu.utils.User
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ScaledTextConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.TextAspectConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.effects.ScissorEffect

object PartyFinderWindow : BaseWindow(false) {
    val primaryColor = UIScheme.pfWindowBackground.toConstraint()

    private val headerHeight = 30.pixels
    private val filterHeight = 60.pixels
    private val spacing = 10f
    private var filtersOpen = false

    lateinit var popup: UIPopup
    lateinit var createButton: UIButton
    lateinit var filterArea : UIContainer

    init {
        create()
        onUpdate()

        registerPartyListChangedEvent { parties ->
            onUpdate()
        }

        registerPartyCreatedEvent { party ->
            if (party.user == User.getUsername()) {
                onUpdate()
            }
        }

        registerMyPartyChangedEvent {
            onUpdate()
        }

        registerErrorMessageEvent { message, origin ->
            if (mc.screen == this && (origin == "/app/party/join" || origin == "/app/party/report" || origin == "/app/party/delete")) {
                if (message == "Target user is not currently connected to the WebSocket.") return@registerErrorMessageEvent
                popup.setText(message)
                popup.showPopup()
            }
        }
    }

    fun create() {
        val radius = 5f

        val background = UIRoundedRectangle(radius).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = 80.percent
            height = 80.percent
            color = primaryColor
        } childOf window

        val useableArea = UIContainer().constrain {
            x = CenterConstraint()
            y = (radius/2).pixels
            width = 100.percent
            height = 100.percent - radius.pixels
        } childOf background

        createHeader(useableArea)
        createFilterArea(useableArea)

        Inspector(window) childOf window
    }

    fun createHeader(background: UIComponent) {
        val header = UIContainer().constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
            width = 100.percent
            height = headerHeight
        } childOf background effect ScissorEffect()

        val textScale = 1.5f

        UIText("RFU Party Finder").constrain {
            x = spacing.pixels
            y = CenterConstraint()
            width = ScaledTextConstraint(textScale)
            height = TextAspectConstraint()
            color = UIScheme.pfTitleText.toConstraint()
        } childOf header

        val rightArea = UIContainer().constrain {
            x = spacing.pixels(true)
            y = CenterConstraint()
            width = 30.percent
            height = 100.percent - 5.pixels
        } childOf header

        createButton = UIButton("Create", radius = 5f) {
            //Open Party creation window (will be a new window)
        }.constrain {
            x = SiblingConstraint(2f, alignOpposite = true)
            y = CenterConstraint()
            height = 100.percent
            width = 45.percent
        } childOf rightArea

        createButton = UIButton("Filters", radius = 5f) {
            filtersOpen = !filtersOpen
            onUpdate()
        }.constrain {
            x = SiblingConstraint(2f, alignOpposite = true)
            y = CenterConstraint()
            height = 100.percent
            width = 45.percent
        } childOf rightArea

        //Separator
        UIBlock().constrain {
            x = CenterConstraint()
            y = 0.pixels(true)
            width = 100.percent - spacing.pixels
            height = 1.pixels
            color = UIScheme.pfWindowSeparator.toConstraint()
        } childOf header
    }

    fun createFilterArea(background: UIComponent) {
        filterArea = UIContainer().constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
            width = 100.percent
            height = filterHeight
        } childOf background effect ScissorEffect()

        //Separator
        UIBlock().constrain {
            x = CenterConstraint()
            y = 0.pixels(true)
            width = 100.percent - spacing.pixels
            height = 1.pixels
            color = UIScheme.pfWindowSeparator.toConstraint()
        } childOf filterArea
    }

    fun createPartyArea() {

    }

    fun onUpdate() {
        if(filtersOpen) {
            filterArea.animate {
                setHeightAnimation(Animations.OUT_EXP, 0.5f, filterHeight)
            }
        } else {
            filterArea.animate {
                setHeightAnimation(Animations.OUT_EXP, 0.5f, 0.pixels)
            }
        }
    }
}