package cloud.glitchdev.rfu.gui.window

import cloud.glitchdev.rfu.constants.SeaCreatures
import cloud.glitchdev.rfu.config.seacreatures.SeaCreatureSettingsManager
import cloud.glitchdev.rfu.gui.UIScheme
import cloud.glitchdev.rfu.gui.components.checkbox.UICheckbox
import cloud.glitchdev.rfu.gui.components.textinput.UIDecoratedTextInput
import cloud.glitchdev.rfu.config.categories.SeaCreatureConfig
import cloud.glitchdev.rfu.constants.RareScDisplayDataType
import cloud.glitchdev.rfu.constants.text.TextColor.GRAY
import cloud.glitchdev.rfu.constants.text.TextColor.WHITE
import cloud.glitchdev.rfu.constants.text.TextColor.YELLOW
import cloud.glitchdev.rfu.constants.text.TextEffects.BOLD
import cloud.glitchdev.rfu.gui.components.elementa.BoundingBoxConstraint
import cloud.glitchdev.rfu.gui.components.elementa.group.GroupMaxSizeConstraint
import cloud.glitchdev.rfu.utils.dsl.toMcCodes
import cloud.glitchdev.rfu.utils.gui.addHoverColoring
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*

class SeaCreatureEditWindow : BaseWindow(true) {
    private val mainContainer: UIRoundedRectangle
    private val searchBar: UIDecoratedTextInput
    private val scrollArea: ScrollComponent
    
    private var currentSc: SeaCreatures? = null
    private val listButtons = mutableMapOf<String, UIComponent>()
    
    // Identity fields
    private val nameInput: UIDecoratedTextInput
    private val pluralInput: UIDecoratedTextInput
    private val articleInput: UIDecoratedTextInput
    private val styleInput: UIDecoratedTextInput
    
    // Settings checkboxes
    private val specialCheckbox: UICheckbox
    private val lsRangeCheckbox: UICheckbox
    private val bossbarCheckbox: UICheckbox
    private val gdragAlertCheckbox: UICheckbox
    private val rareSCAlertCheckbox: UICheckbox
    private val scDisplayColorInput: UIDecoratedTextInput

    // Preview texts
    private val previewNormal: UIText
    private val previewDouble: UIText
    private val previewDisplay: UIText

    init {
        mainContainer = UIRoundedRectangle(5f).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = 80.percent()
            height = 80.percent()
            color = UIScheme.windowBackground.toConstraint()
        } childOf window

        // --- Sidebar ---
        val sidebar = UIRoundedRectangle(2f).constrain {
            x = 5.pixels()
            y = 5.pixels()
            width = 150.pixels()
            height = 100.percent() - 10.pixels()
            color = UIScheme.sidebarBackground.toConstraint()
        } childOf mainContainer

        searchBar = UIDecoratedTextInput("Search...", 2f).constrain {
            x = 0.pixels
            y = 5.pixels()
            width = 100.percent() - 5.pixels
            height = 18.pixels()
        } childOf sidebar
        
        searchBar.onChange = { query ->
            refreshList(query)
        }

        scrollArea = ScrollComponent(verticalScrollEnabled = true).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
            width = 100.percent()
            height = 100.percent() - 28.pixels()
        } childOf sidebar

        val scrollbar = UIRoundedRectangle(2f).constrain {
            x = 0.pixels(true)
            y = 0.pixels()
            width = 3.pixels()
            height = 100.percent()
            color = UIScheme.secondaryColor.toConstraint()
        } childOf sidebar

        scrollArea.setScrollBarComponent(scrollbar, hideWhenUseless = true, isHorizontal = false)

        // --- Content Area ---
        val contentArea = UIContainer().constrain {
            x = SiblingConstraint(5f)
            y = 5.pixels()
            width = 100.percent() - 165.pixels()
            height = 100.percent() - 10.pixels()
        } childOf mainContainer

        // Header
        val header = UIContainer().constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 25.pixels()
        } childOf contentArea

        UIText("Edit Sea Creatures").constrain {
            x = 5.pixels()
            y = CenterConstraint()
            color = UIScheme.primaryTextColor.toConstraint()
        } childOf header

        UIRoundedRectangle(5f).constrain {
            x = 0.pixels(true)
            y = CenterConstraint()
            width = 20.pixels()
            height = 20.pixels()
            color = UIScheme.denyColor.toConstraint()
        }.addHoverColoring(
            Animations.OUT_EXP,
            UIScheme.HOVER_EFFECT_DURATION,
            UIScheme.denyColor.toConstraint(),
            UIScheme.denyColor.brighter().toConstraint()
        ).onMouseClick {
            closeScreen()
        }.apply {
            UIText("X").constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                color = UIScheme.primaryTextColor.toConstraint()
            } childOf this
        } childOf header

        // Edit Area Scrollable Content
        val editScrollBarContainer = UIContainer().constrain {
            x = CenterConstraint()
            y = 30.pixels()
            width = 100.percent()
            height = 100.percent() - 30.pixels()
        } childOf contentArea

        val editScrollArea = ScrollComponent(verticalScrollEnabled = true).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = 100.percent()
            height = 100.percent()
        } childOf editScrollBarContainer

        val editScrollbar = UIRoundedRectangle(2f).constrain {
            x = 0.pixels(true)
            y = 0.pixels()
            width = 3.pixels()
            height = 100.percent()
            color = UIScheme.secondaryColor.toConstraint()
        } childOf editScrollBarContainer

        editScrollArea.setScrollBarComponent(editScrollbar, hideWhenUseless = true, isHorizontal = false)

        val content = UIRoundedRectangle(2f).constrain {
            x = CenterConstraint()
            y = 0.pixels()
            width = 100.percent()
            height = BoundingBoxConstraint()
            color = UIScheme.contentBackground.toConstraint()
        } childOf editScrollArea

        // Identity Section
        UIText("Identity").constrain {
            x = 10.pixels()
            y = 10.pixels()
            color = UIScheme.primaryTextColor.toConstraint()
        } childOf content

        fun addField(label: String, topPadding: Float = 5f): UIDecoratedTextInput {
            val container = UIContainer().constrain {
                x = 0.pixels()
                y = SiblingConstraint(topPadding)
                width = 100.percent()
                height = ChildBasedMaxSizeConstraint()
            } childOf content

            val textContainer = UIContainer().constrain {
                x = 15.pixels()
                y = CenterConstraint()
                width = GroupMaxSizeConstraint("SCEditWindowField", ChildBasedMaxSizeConstraint())
                height = ChildBasedSizeConstraint()
            } childOf container

            UIText(label).constrain {
                x = 0.pixels
                y = CenterConstraint()
                width = ScaledTextConstraint(1f)
                height = TextAspectConstraint()
                color = UIScheme.secondaryTextColor.toConstraint()
            } childOf textContainer
            
            return UIDecoratedTextInput("", 2f).constrain {
                x = SiblingConstraint(2f)
                y = CenterConstraint()
                width = FillConstraint() - 32.pixels
                height = 16.pixels()
            } childOf container
        }

        nameInput = addField("Name:", 10f)
        pluralInput = addField("Plural:")
        articleInput = addField("Article:")
        styleInput = addField("Catch Color:")

        // Preview Section
        previewNormal = UIText("Preview: ").constrain {
            x = 15.pixels()
            y = SiblingConstraint(10f)
        } childOf content

        previewDouble = UIText("Preview: ").constrain {
            x = 15.pixels()
            y = SiblingConstraint(5f)
        } childOf content

        scDisplayColorInput = addField("Display Color:", 10f)
        
        previewDisplay = UIText("Preview: ").constrain {
            x = 15.pixels()
            y = SiblingConstraint(10f)
        } childOf content

        // Settings Section
        UIText("Settings").constrain {
            x = 10.pixels()
            y = SiblingConstraint(15f)
            color = UIScheme.primaryTextColor.toConstraint()
        } childOf content

        specialCheckbox = UICheckbox("Rare", false) { isRare ->
            lsRangeCheckbox.state = isRare
            bossbarCheckbox.state = isRare
            gdragAlertCheckbox.state = isRare
            rareSCAlertCheckbox.state = isRare
            refreshEnabledStates()
            saveCurrent() 
        }.constrain {
            x = 15.pixels()
            y = SiblingConstraint(10f)
            width = 100.pixels()
            height = 15.pixels()
        } childOf content

        lsRangeCheckbox = UICheckbox("Lootshare Range", false) { saveCurrent() }.constrain {
            x = 15.pixels()
            y = SiblingConstraint(5f)
            width = 150.pixels()
            height = 15.pixels()
        } childOf content

        bossbarCheckbox = UICheckbox("Bossbar", false) { saveCurrent() }.constrain {
            x = 15.pixels()
            y = SiblingConstraint(5f)
            width = 100.pixels()
            height = 15.pixels()
        } childOf content

        gdragAlertCheckbox = UICheckbox("Gdrag Alert", false) { saveCurrent() }.constrain {
            x = 15.pixels()
            y = SiblingConstraint(5f)
            width = 100.pixels()
            height = 15.pixels()
        } childOf content

        rareSCAlertCheckbox = UICheckbox("Rare Alert", false) { saveCurrent() }.constrain {
            x = 15.pixels()
            y = SiblingConstraint(5f)
            width = 100.pixels()
            height = 15.pixels()
        } childOf content

        // Add change listeners to inputs
        nameInput.onChange = { saveCurrent() }
        pluralInput.onChange = { saveCurrent() }
        articleInput.onChange = { saveCurrent() }
        styleInput.onChange = { saveCurrent() }
        scDisplayColorInput.onChange = { saveCurrent() }

        refreshList("")
        
        // Load first one by default if exists
        SeaCreatures.entries.firstOrNull()?.let { loadSc(it) }
    }

    private fun refreshList(query: String) {
        scrollArea.clearChildren()
        listButtons.clear()

        SeaCreatures.entries
            .filter { it.scName.contains(query, ignoreCase = true) }
            .sortedBy { it.scName }
            .forEach { sc ->
                val container = UIContainer().constrain {
                    x = 0.pixels()
                    y = SiblingConstraint()
                    width = 100.percent()
                    height = 20.pixels()
                }.onMouseClick {
                    loadSc(sc)
                } childOf scrollArea

                val text = UIText(sc.scName).constrain {
                    x = 5.pixels()
                    y = CenterConstraint()
                    color = (if (sc == currentSc) UIScheme.selectedTextColor else UIScheme.primaryTextColor).toConstraint()
                } childOf container

                container.onMouseEnter {
                    text.animate {
                        setColorAnimation(Animations.OUT_EXP, UIScheme.HOVER_EFFECT_DURATION, UIScheme.secondaryColor.toConstraint())
                    }
                }.onMouseLeave {
                    text.animate {
                        setColorAnimation(Animations.OUT_EXP, UIScheme.HOVER_EFFECT_DURATION, (if (sc == currentSc) UIScheme.selectedTextColor else UIScheme.primaryTextColor).toConstraint())
                    }
                }

                listButtons[sc.scName] = text
            }
    }

    private fun loadSc(sc: SeaCreatures?) {
        if (sc == null) return
        
        // Update highlight in list
        currentSc?.let { oldSc ->
            listButtons[oldSc.scName]?.let { (it as UIText).constrain { color = UIScheme.primaryTextColor.toConstraint() } }
        }
        currentSc = sc
        listButtons[sc.scName]?.let { (it as UIText).constrain { color = UIScheme.selectedTextColor.toConstraint() } }
        
        // Load data
        nameInput.setText(sc.scName)
        pluralInput.setText(sc.getPluralName())
        articleInput.setText(sc.getArticle())
        styleInput.setText(sc.getStyleCode().replace("§", "&"))
        
        specialCheckbox.state = sc.special
        lsRangeCheckbox.state = sc.lsRangeEnabled
        bossbarCheckbox.state = sc.bossbar
        gdragAlertCheckbox.state = sc.gdragAlert
        rareSCAlertCheckbox.state = sc.rareSCAlert
        scDisplayColorInput.setText(sc.scDisplayColor.replace("§", "&"))
        
        refreshEnabledStates()
        updatePreviews()
    }

    private fun refreshEnabledStates() {
        val isRare = specialCheckbox.state
        lsRangeCheckbox.isEnabled = isRare
        bossbarCheckbox.isEnabled = isRare
        gdragAlertCheckbox.isEnabled = isRare
        rareSCAlertCheckbox.isEnabled = isRare
    }

    private fun updatePreviews() {
        val sc = currentSc ?: return
        val style = styleInput.getText()
        val name = nameInput.getText()
        val plural = pluralInput.getText()
        val article = articleInput.getText()
        val articleUpper = article.replaceFirstChar { it.uppercaseChar() }
        val mob = if (article.isNotEmpty()) "$article $name" else name
        val displayColor = scDisplayColorInput.getText().toMcCodes().ifEmpty { WHITE }

        val normalTemplate = SeaCreatureConfig.catchMessageTemplate
        val doubleTemplate = SeaCreatureConfig.doubleHookCatchMessageTemplate

        fun style(template: String): String {
            return template
                .replace("{article}", article)
                .replace("{article_upper}", articleUpper)
                .replace("{name}", name)
                .replace("{style}", style)
                .replace("{plural}", plural)
                .replace("{mob}", mob)
                .replace("{mobs}", plural)
                .toMcCodes()
        }

        previewNormal.setText("Preview: ${style(normalTemplate)}")
        previewDouble.setText("Preview: ${style(doubleTemplate)}")

        val dataOrder = SeaCreatureConfig.rareScDisplayDataOrder
        val displayPreviewLine = buildString {
            append("$displayColor${BOLD}$name:")
            dataOrder.forEach { dataType ->
                when (dataType) {
                    RareScDisplayDataType.STREAK -> append(" ${YELLOW}5")
                    RareScDisplayDataType.AVERAGE -> append(" ${GRAY}(${YELLOW}20$GRAY)")
                    RareScDisplayDataType.TOTAL -> append(" $displayColor[${YELLOW}10$displayColor]")
                    RareScDisplayDataType.TIME_SINCE -> append(" ${WHITE}10s")
                }
            }
        }
        previewDisplay.setText("Preview: $displayPreviewLine")
    }

    private fun saveCurrent() {
        val sc = currentSc ?: return
        
        SeaCreatureSettingsManager.updateCreature(sc.scName) { current ->
            current.copy(
                name = nameInput.getText(),
                plural = pluralInput.getText(),
                article = articleInput.getText(),
                style = styleInput.getText().toMcCodes(),
                special = specialCheckbox.state,
                lsRangeEnabled = lsRangeCheckbox.state,
                bossbar = bossbarCheckbox.state,
                gdragAlert = gdragAlertCheckbox.state,
                rareSCAlert = rareSCAlertCheckbox.state,
                scDisplayColor = scDisplayColorInput.getText().toMcCodes()
            )
        }
        
        updatePreviews()
        SeaCreatureSettingsManager.save()
    }
}
