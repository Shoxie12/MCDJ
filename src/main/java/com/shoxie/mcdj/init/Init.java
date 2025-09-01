package com.shoxie.mcdj.init;

import com.shoxie.mcdj.block.MusicGeneratorBlock;
import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.item.CustomDiscItem;
import com.shoxie.mcdj.item.ObsidianPlateItem;
import com.shoxie.mcdj.item.SealedRecordItem;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.menu.MusicGeneratorMenu;
import com.shoxie.mcdj.misc.MusicFile;
import com.shoxie.mcdj.entity.MusicGeneratorEntity;
import com.shoxie.mcdj.misc.discRegData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.ArrayList;

import static com.shoxie.mcdj.Lib.*;
import static com.shoxie.mcdj.mcdj.RECORDS_DATA;

public class Init {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, mcdj.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, mcdj.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, mcdj.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, mcdj.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, mcdj.MODID);

    //Blocks, BIs, BEs
    public static final RegistryObject<MusicGeneratorBlock> MUSIC_GENERATOR = BLOCKS.register("music_generator", () -> new MusicGeneratorBlock(BlockBehaviour.Properties.of().strength(3.0f, 10f)));

    public static final RegistryObject<Item> MUSIC_GENERATOR_ITEM =
            ITEMS.register("music_generator", () -> new BlockItem(MUSIC_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<MusicGeneratorEntity>> MUSIC_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("music_generator",
                    () -> BlockEntityType.Builder.of(MusicGeneratorEntity::new, MUSIC_GENERATOR.get())
                            .build(null));

    //SoundEvents
    public static final RegistryObject<SoundEvent> BLANK_RECORD_SOUND_EVENT = registerSoundEvent(mcdj.DEFAULT_BLANK_RECORD_SOUND);

    //GeneratedSoundEvents
    public static final ArrayList<RegistryObject<SoundEvent>> CUSTOM_RECORD_SOUND_EVENTS = registerGeneratedSounds();

    //Items
    public static final RegistryObject<ObsidianPlateItem> OBSIDIAN_PLATE =
            ITEMS.register(ObsidianPlateItem.name, ObsidianPlateItem::new);
    public static final RegistryObject<BlankDiscItem> BLANK_RECORD =
            ITEMS.register(BlankDiscItem.name, BlankDiscItem::new);
    public static final RegistryObject<SealedRecordItem> SEALED_RECORD_ITEM =
            ITEMS.register(SealedRecordItem.name, SealedRecordItem::new);

    //GeneratedItems
    public static final ArrayList<RegistryObject<CustomDiscItem>> CUSTOM_RECORD_ITEMS = registerGeneratedCustomRecordItems();

    //Menus
    public static final RegistryObject<MenuType<MusicGeneratorMenu>> MUSIC_GENERATOR_MENU = MENU_TYPES.register("music_generator",
            () -> IForgeMenuType.create(MusicGeneratorMenu::new));

    public static @NotNull ArrayList<RegistryObject<CustomDiscItem>> registerGeneratedCustomRecordItems() {
        if (RECORDS_DATA == null || RECORDS_DATA.isEmpty() || CUSTOM_RECORD_SOUND_EVENTS.isEmpty())
            return new ArrayList<>();
        ArrayList<RegistryObject<CustomDiscItem>> recList = new ArrayList<>();

        for (int i = 0; i < RECORDS_DATA.size(); i++) {
            int _i = i;
            int d = getAudioDuration(Paths.get(getPlaylistStreamingPath()+RECORDS_DATA.get(_i).getFileName()+".ogg").toFile());
            recList.add(ITEMS.register(RECORDS_DATA.get(_i).getName(), () -> new CustomDiscItem(RECORDS_DATA.get(_i).getName(), CUSTOM_RECORD_SOUND_EVENTS.get(_i), d, RECORDS_DATA.get(_i).getNumber())));
            mcdj.currentPlaylist.add(
                    new MusicFile(RECORDS_DATA.get(_i).getDisplayName(), RECORDS_DATA.get(_i).getName(), RECORDS_DATA.get(_i).getFilePath(),
                            d, true, RECORDS_DATA.get(_i).getNumber()));

        }
        return recList;
    }

    public static @NotNull ArrayList<RegistryObject<SoundEvent>> registerGeneratedSounds() {
        if (RECORDS_DATA == null || RECORDS_DATA.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<RegistryObject<SoundEvent>> snds = new ArrayList<>();
        for (discRegData iRecData : RECORDS_DATA) {
            snds.add(registerSoundEvent(iRecData.getName()));
        }
        return snds;
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(final String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(mcdj.MODID, name)));
    }
}