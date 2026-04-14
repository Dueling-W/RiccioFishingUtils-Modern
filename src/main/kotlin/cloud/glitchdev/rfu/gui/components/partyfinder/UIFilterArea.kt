package cloud.glitchdev.rfu.gui.components.partyfinder

import cloud.glitchdev.rfu.constants.FishingIslands
import cloud.glitchdev.rfu.constants.LiquidTypes
import cloud.glitchdev.rfu.gui.UIScheme
import cloud.glitchdev.rfu.gui.components.checkbox.UICheckbox
import cloud.glitchdev.rfu.gui.components.textinput.UIDecoratedTextInput
import cloud.glitchdev.rfu.gui.components.checkbox.UIRadio
import cloud.glitchdev.rfu.gui.components.colors
import cloud.glitchdev.rfu.gui.components.dropdown.UIDropdown
import cloud.glitchdev.rfu.gui.components.elementa.CramAwareMaxSizeConstraint
import cloud.glitchdev.rfu.model.party.FishingParty
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.CramSiblingConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.YConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.div
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.times
import gg.essential.elementa.dsl.toConstraint

class UIFilterArea(private val filterHeight : YConstraint, var onFilterChange: () -> Unit = {}) : UIContainer() {
    lateinit var searchField: UIDecoratedTextInput
    lateinit var levelField: UIDecoratedTextInput
    lateinit var canJoinField: UICheckbox
    lateinit var liquidField: UIRadio
    lateinit var killerField: UICheckbox
    lateinit var endermanField: UICheckbox
    lateinit var lootingField: UICheckbox
    lateinit var brainFoodField: UICheckbox

    init {
        create()
        createInteractions()
    }

    fun create() {
        val topArea = UIContainer().constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
            width = 96.percent()
            height = filterHeight / 3
        } childOf this

        searchField = UIDecoratedTextInput("Search", 2f).constrain {
            x = SiblingConstraint(2f)
            y = CenterConstraint()
            width = 90.percent() - 2.pixels()
            height = 80.percent()
        }.colors {
            primaryColor = UIScheme.pfInputBg.toConstraint()
            hoverColor = UIScheme.pfInputBgHovered.toConstraint()
        } childOf topArea

        levelField = UIDecoratedTextInput("LVL", 2f, true, 3).constrain {
            x = SiblingConstraint(2f)
            y = CenterConstraint()
            width = 10.percent()
            height = 80.percent()
        }.colors {
            primaryColor = UIScheme.pfInputBg.toConstraint()
            hoverColor = UIScheme.pfInputBgHovered.toConstraint()
        } childOf topArea

        val bottomArea = UIContainer().constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
            width = 96.percent()
            height = CramAwareMaxSizeConstraint()
        } childOf this

        canJoinField = UICheckbox("Can Join").constrain {
            x = CramSiblingConstraint(4f)
            y = CramSiblingConstraint()
            width = ChildBasedSizeConstraint()
            height = filterHeight * 0.4
        }.colors {
            primaryColor = UIScheme.pfInputBg.toConstraint()
            hoverColor = UIScheme.pfInputBgHovered.toConstraint()
        } childOf bottomArea

        liquidField = UIRadio(LiquidTypes.toDataOptions(), 0).constrain {
            x = CramSiblingConstraint(4f)
            y = CramSiblingConstraint()
            width = 80.pixels()
            height = filterHeight * 0.4
        }.colors {
            primaryColor = UIScheme.pfInputBg.toConstraint()
            hoverColor = UIScheme.pfInputBgHovered.toConstraint()
        } childOf bottomArea

        killerField = UICheckbox("Has Killer").constrain {
            x = CramSiblingConstraint(4f)
            y = CramSiblingConstraint()
            width = ChildBasedSizeConstraint()
            height = filterHeight * 0.4
        }.colors {
            primaryColor = UIScheme.pfInputBg.toConstraint()
            hoverColor = UIScheme.pfInputBgHovered.toConstraint()
        } childOf bottomArea

        endermanField = UICheckbox("Enderman 9").constrain {
            x = CramSiblingConstraint(4f)
            y = CramSiblingConstraint()
            width = ChildBasedSizeConstraint()
            height = filterHeight * 0.4
        }.colors {
            primaryColor = UIScheme.pfInputBg.toConstraint()
            hoverColor = UIScheme.pfInputBgHovered.toConstraint()
        } childOf bottomArea

        lootingField = UICheckbox("Looting 5").constrain {
            x = CramSiblingConstraint(4f)
            y = CramSiblingConstraint()
            width = ChildBasedSizeConstraint()
            height = filterHeight * 0.4
        }.colors {
            primaryColor = UIScheme.pfInputBg.toConstraint()
            hoverColor = UIScheme.pfInputBgHovered.toConstraint()
        } childOf bottomArea

        brainFoodField = UICheckbox("Brain Food").constrain {
            x = CramSiblingConstraint(4f)
            y = CramSiblingConstraint()
            width = ChildBasedSizeConstraint()
            height = filterHeight * 0.4
        }.colors {
            primaryColor = UIScheme.pfInputBg.toConstraint()
            hoverColor = UIScheme.pfInputBgHovered.toConstraint()
        } childOf bottomArea
    }

    fun createInteractions() {
        searchField.onChange = {
            onFilterChange()
        }
        levelField.onChange = {
            onFilterChange()
        }
        liquidField.onChange = {
            onFilterChange()
        }
        killerField.onChange = {
            onFilterChange()
        }
        endermanField.onChange = {
            onFilterChange()
        }
        lootingField.onChange = {
            onFilterChange()
        }
        brainFoodField.onChange = {
            onFilterChange()
        }
    }

    fun applyFilter(parties: List<FishingParty>): MutableList<FishingParty> {
        return parties.filter { party ->
            val text = searchField.getText().lowercase()
            if (!(
                text.isEmpty() ||
                party.island.island.lowercase().contains(text) ||
                party.title.lowercase().contains(text) ||
                party.description.lowercase().contains(text)
            )) return@filter false
            if (levelField.getText().isNotEmpty() && party.level < levelField.getText().toInt()) return@filter false
            if (party.liquid != liquidField.getSelectedValue().value) return@filter false
            if (killerField.state && !party.getRequisite("has_killer", "Has Killer").value) return@filter false
            if (endermanField.state && !party.getRequisite("enderman_9", "Enderman 9").value) return@filter false
            if (lootingField.state && !party.getRequisite("looting_5", "Looting 5").value) return@filter false
            if (brainFoodField.state && !party.getRequisite("brain_food", "Brain Food").value) return@filter false

            return@filter true
        } as MutableList<FishingParty>
    }
}