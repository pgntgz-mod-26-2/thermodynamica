package com.Tribulla.thermodynamica.resource;

import com.Tribulla.thermodynamica.Thermodynamica;
import com.Tribulla.thermodynamica.api.ThermalProperties;
import com.Tribulla.thermodynamica.config.ThermalPropertiesRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class ThermalPropertyResourceLoader extends SimpleJsonResourceReloadListener<JsonElement> {
    private final ThermalPropertiesRegistry registry;

    public ThermalPropertyResourceLoader(ThermalPropertiesRegistry registry) {
        super(ExtraCodecs.JSON, FileToIdConverter.json("thermal_properties"));
        this.registry = registry;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> object, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        // Since we don't have a clearSource for thermal properties yet, we'll just
        // overwrite
        // OR we should ideally add a way to clear them if they came from data packs.

        for (Map.Entry<Identifier, JsonElement> entry : object.entrySet()) {
            Identifier fileLocation = entry.getKey();
            try {
                JsonObject json = entry.getValue().getAsJsonObject();

                if (json.has("blocks")) {
                    JsonObject blocks = json.getAsJsonObject("blocks");
                    for (Map.Entry<String, JsonElement> blockEntry : blocks.entrySet()) {
                        Identifier blockId = Identifier.tryParse(blockEntry.getKey());
                        if (blockId != null) {
                            registry.registerOverride(blockId, parseProps(blockEntry.getValue().getAsJsonObject()));
                        }
                    }
                }

                // Add support for direct block files if preferred:
                // e.g. "thermodynamica:thermal_properties/minecraft/cobblestone.json"
                if (json.has("conductivity") || json.has("heat_capacity") || json.has("temperature")) {
                    registry.registerOverride(fileLocation, parseProps(json));
                }

            } catch (Exception e) {
                Thermodynamica.LOGGER.error("Failed to load thermal properties file {}: {}", fileLocation,
                        e.getMessage());
            }
        }
        Thermodynamica.LOGGER.info("Loaded thermal properties from data packs");
    }

    private ThermalProperties parseProps(JsonObject obj) {
        double conductivity = obj.has("conductivity") ? obj.get("conductivity").getAsDouble()
                : ThermalProperties.DEFAULT_CONDUCTIVITY;
        double heatCapacity = obj.has("heat_capacity") ? obj.get("heat_capacity").getAsDouble()
                : ThermalProperties.DEFAULT_HEAT_CAPACITY;
        double dissipationRate = obj.has("dissipation_rate") ? obj.get("dissipation_rate").getAsDouble()
                : ThermalProperties.DEFAULT_DISSIPATION_RATE;
        java.util.OptionalDouble temperature = obj.has("temperature") ? java.util.OptionalDouble.of(obj.get("temperature").getAsDouble()) : java.util.OptionalDouble.empty();
        return new ThermalProperties(conductivity, heatCapacity, dissipationRate, temperature);
    }
}
