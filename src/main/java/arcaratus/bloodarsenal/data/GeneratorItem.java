package arcaratus.bloodarsenal.data;

import arcaratus.bloodarsenal.common.BloodArsenal;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static arcaratus.bloodarsenal.common.item.ModItems.*;

/**
 * Code partially adapted from Botania
 * https://github.com/Vazkii/Botania
 */
public class GeneratorItem extends ItemModelProvider
{
    public GeneratorItem(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, BloodArsenal.MOD_ID, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "Blood Arsenal item models";
    }

    @Override
    protected void registerModels()
    {
        Set<Item> items = Registry.ITEM.stream().filter(i -> BloodArsenal.MOD_ID.equals(Registry.ITEM.getKey(i).getNamespace()))
                .collect(Collectors.toSet());
        registerItemBlocks(takeAll(items, i -> i instanceof BlockItem).stream().map(i -> (BlockItem) i).collect(Collectors.toSet()));
        registerItemOverrides(items);
        registerItems(items);
    }

    private static String name(Item i)
    {
        return Registry.ITEM.getKey(i).getPath();
    }

    private static final ResourceLocation GENERATED = new ResourceLocation("item/generated");
    private static final ResourceLocation HANDHELD = new ResourceLocation("item/handheld");

    private ItemModelBuilder handheldItem(String name)
    {
        return withExistingParent(name, HANDHELD).texture("layer0", BloodArsenal.rl("item/" + name));
    }

    private ItemModelBuilder handheldItem(Item i)
    {
        return handheldItem(name(i));
    }

    private ItemModelBuilder generatedItem(String name)
    {
        return withExistingParent(name, GENERATED)
                .texture("layer0", BloodArsenal.rl("item/" + name));
    }

    private ItemModelBuilder generatedItem(Item i)
    {
        return generatedItem(name(i));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Collection<T> takeAll(Set<? extends T> src, T... items)
    {
        List<T> ret = Arrays.asList(items);
        for (T item : items)
        {
            if (!src.contains(item))
            {
                BloodArsenal.LOGGER.warn("Item {} not found in set", item);
            }
        }

        if (!src.removeAll(ret))
        {
            BloodArsenal.LOGGER.warn("takeAll array didn't yield anything ({})", Arrays.toString(items));
        }

        return ret;
    }

    public static <T> Collection<T> takeAll(Set<T> src, Predicate<T> pred)
    {
        List<T> ret = new ArrayList<>();

        Iterator<T> iter = src.iterator();
        while (iter.hasNext())
        {
            T item = iter.next();
            if (pred.test(item))
            {
                iter.remove();
                ret.add(item);
            }
        }

        if (ret.isEmpty())
        {
            BloodArsenal.LOGGER.warn("takeAll predicate yielded nothing", new Throwable());
        }

        return ret;
    }

    private void registerItemBlocks(Set<BlockItem> itemBlocks)
    {
        itemBlocks.forEach(i -> {
            String name = Registry.ITEM.getKey(i).getPath();
            withExistingParent(name, BloodArsenal.rl("block/" + name));
        });
    }

    private void registerItemOverrides(Set<Item> items)
    {

    }

    private void registerItems(Set<Item> items)
    {
        takeAll(items, BLOOD_INFUSED_WOODEN_AXE.get(), BLOOD_INFUSED_WOODEN_PICKAXE.get(), BLOOD_INFUSED_WOODEN_SHOVEL.get(), BLOOD_INFUSED_WOODEN_SWORD.get(),
                BLOOD_INFUSED_IRON_AXE.get(), BLOOD_INFUSED_IRON_PICKAXE.get(), BLOOD_INFUSED_IRON_SHOVEL.get(), BLOOD_INFUSED_IRON_SWORD.get()).forEach(this::handheldItem);

        items.forEach(this::generatedItem);
    }
}
