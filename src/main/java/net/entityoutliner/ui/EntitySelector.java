package net.entityoutliner.ui;

import net.entityoutliner.EntityOutliner;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EntitySelector extends Screen {
    protected final Screen parent;

    private TextFieldWidget searchField;
    private EntityListWidget list;
    private static boolean groupByCategory = true;
    private static String searchText = "";
    private static Map<String, List<EntityType<?>>> searcher = new HashMap<>(); // Prefix -> arr of results

    public EntitySelector(@Nullable Screen parent) {
        super(Text.translatable("title.entity-outliner.selector"));
        this.parent = parent;
        this.initializePrefixTree();
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    protected void init() {
        // Create list with full width to ensure full background
        this.list = new EntityListWidget(this.client, this.width, this.height - 64, 32, 25);
        this.addDrawableChild(list);

        // Create search field
        this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 6, 200, 20, Text.of(searchText));
        this.searchField.setText(searchText);
        this.searchField.setChangedListener(this::onSearchFieldUpdate);
        this.addDrawableChild(searchField);

        // Create buttons
        List<ButtonWidget.Builder> buttons = List.of(
            // sort type
            ButtonWidget.builder(
                Text.translatable(groupByCategory ? "button.entity-outliner.categories" : "button.entity-outliner.no-categories"),
                (button) -> {
                    groupByCategory = !groupByCategory;
                    this.onSearchFieldUpdate(this.searchField.getText());
                    button.setMessage(Text.translatable(groupByCategory ? "button.entity-outliner.categories" : "button.entity-outliner.no-categories"));
                }
            ),

            // deselect
            ButtonWidget.builder(
                Text.translatable("button.entity-outliner.deselect"),
                (button) -> {
                    EntityOutliner.entityTypeOutlineConfig.clear();
                    this.onSearchFieldUpdate(this.searchField.getText());
                }
            ),

            // toggle outlining
            ButtonWidget.builder(
                Text.translatable(EntityOutliner.outliningEntities ? "button.entity-outliner.on" : "button.entity-outliner.off"),
                (button) -> {
                    EntityOutliner.outliningEntities = !EntityOutliner.outliningEntities;
                    button.setMessage(Text.translatable(EntityOutliner.outliningEntities ? "button.entity-outliner.on" : "button.entity-outliner.off"));
                }
            ),

            // toggle team colors
            ButtonWidget.builder(
                Text.translatable("button.entity-outliner.team-color-on"),
                (button) -> {
                    EntityOutliner.preferTeamColor = !EntityOutliner.preferTeamColor;
                    button.setMessage(Text.translatable(EntityOutliner.preferTeamColor ? "button.entity-outliner.team-color-on" : "button.entity-outliner.team-color-off"));
                }
            ),

            // done
            ButtonWidget.builder(
                Text.translatable("button.entity-outliner.done"),
                (button) -> this.close()
            )
        );

        final int width = 80;
        final int height = 20;
        final int offset = (this.width / buttons.size());
        int x = (offset - width) / 2;
        final int y = this.height - 16 - (height / 2);
        for (ButtonWidget.Builder builder : buttons) {
            this.addDrawableChild(builder.size(width, height)
                .position(x, y)
                .build()
            );
            x += offset;
        }

        this.setInitialFocus(this.searchField);
        this.onSearchFieldUpdate(this.searchField.getText());
    }

    // Initializes the prefix tree used for searching in the entity selector screen
    private void initializePrefixTree() {
        searcher = new HashMap<>();

        // Initialize no-text results
        List<EntityType<?>> allResults = new ArrayList<>();
        searcher.put("", allResults);

        // Get sorted list of entity types
        List<EntityType<?>> entityTypes = new ArrayList<>();
        for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
            entityTypes.add(entityType);
        }
        entityTypes.sort(Comparator.comparing(o -> o.getName().getString()));

        // Add each entity type to everywhere it belongs in the prefix "tree"
        for (EntityType<?> entityType : entityTypes) {

            String name = entityType.getName().getString().toLowerCase();
            allResults.add(entityType);

            List<String> prefixes = new ArrayList<>();
            prefixes.add("");

            // By looping over the name's length, we add to every possible prefix
            for (int i = 0; i < name.length(); i++) {
                char character = name.charAt(i);

                // Loop over every prefix
                for (int p = 0; p < prefixes.size(); p++) {
                    String prefix = prefixes.get(p) + character;
                    prefixes.set(p, prefix);

                    // Get results for current prefix
                    List<EntityType<?>> results;
                    if (searcher.containsKey(prefix)) {
                        results = searcher.get(prefix);
                    } else {
                        results = new ArrayList<>();
                        searcher.put(prefix, results);
                    }

                    results.add(entityType);
                }

                // Add another prefix to allow searching by second/third/... word
                if (Character.isWhitespace(character)) {
                    prefixes.add("");
                }
            }
        }
    }

    // Callback provided to TextFieldWidget triggered when its text updates
    private void onSearchFieldUpdate(@Nonnull String text) {
        searchText = text;
        text = text.toLowerCase().trim();

        this.list.clearListEntries();

        if (searcher.containsKey(text)) {
            List<EntityType<?>> results = searcher.get(text);

            // Splits results into categories and separates them with headers
            if (groupByCategory) {
                HashMap<SpawnGroup, List<EntityType<?>>> resultsByCategory = new HashMap<>();

                for (EntityType<?> entityType : results) {
                    SpawnGroup category = entityType.getSpawnGroup();
                    if (!resultsByCategory.containsKey(category)) {
                        resultsByCategory.put(category, new ArrayList<>());
                    }

                    resultsByCategory.get(category).add(entityType);
                }

                for (SpawnGroup category : SpawnGroup.values()) {
                    if (resultsByCategory.containsKey(category)) {
                        this.list.addListEntry(EntityListWidget.HeaderEntry.create(category, this.textRenderer));

                        for (EntityType<?> entityType : resultsByCategory.get(category)) {
                            this.list.addListEntry(EntityListWidget.EntityEntry.create(entityType, this.textRenderer));
                        }

                    }
                }

            } else {
                for (EntityType<?> entityType : results) {
                    this.list.addListEntry(EntityListWidget.EntityEntry.create(entityType, this.textRenderer));
                }
            }
        } else { // If there are no results, let the user know
            this.list.addListEntry(EntityListWidget.HeaderEntry.create(null, this.textRenderer));
        }

        // This prevents an overscroll when the user is already scrolled down and the results list is shortened
        this.list.setScrollAmount(this.list.getScrollAmount());
    }

    // Called when config screen is escaped
    @Override
    public void removed() {
        EntityOutliner.saveConfig();
    }

    @Override
    public void renderBackground(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.renderInGameBackground(context);
    }

    // Sends mouseDragged event to the scrolling list
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
