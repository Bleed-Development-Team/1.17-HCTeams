package net.frozenorb.foxtrot.util;

import com.google.common.reflect.TypeToken;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InventorySerialization {

	private static final Type TYPE = new TypeToken<ItemStack[]>() {}.getType();

	public static BasicDBObject serialize(ItemStack[] armor, ItemStack[] inventory) {
		BasicDBList armorDBObject = serialize(armor);
		BasicDBList inventoryDBObject = serialize(inventory);

		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("ArmorContents", armorDBObject);
		dbObject.put("InventoryContents", inventoryDBObject);

		return dbObject;
	}

	// LMFAO
	public static BasicDBList serialize(ItemStack[] items) {
		List<ItemStack> kits = new ArrayList<>(Arrays.asList(items));
		kits.removeIf(Objects::isNull);
		return (BasicDBList) JSON.parse(Foxtrot.PLAIN_GSON.toJson(kits.toArray(new ItemStack[kits.size()])));
	}

	public static ItemStack[] deserialize(BasicDBList dbList) {
		return Foxtrot.PLAIN_GSON.fromJson(Foxtrot.PLAIN_GSON.toJson(dbList), TYPE);
	}

}
