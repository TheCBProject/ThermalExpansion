package cofh.thermalexpansion.proxy;

import cofh.api.core.IModelRegister;
import cofh.thermalexpansion.init.TEFlorbs;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.render.item.ModelFlorb;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class ProxyClient extends Proxy {

	/* INIT */
	@Override
	public void preInit(FMLPreInitializationEvent event) {

		super.preInit(event);

		for (int i = 0; i < modelList.size(); i++) {
			modelList.get(i).registerModels();
		}
		registerRenderInformation();
	}

	@Override
	public void initialize(FMLInitializationEvent event) {

		super.initialize(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		super.postInit(event);
	}

	/* REGISTRATION */
	public void registerRenderInformation() {

		ModelLoaderRegistry.registerLoader(ModelFlorb.LoaderFlorb.INSTANCE);

		ModelLoader.setCustomModelResourceLocation(TEFlorbs.itemFlorb, 0, ModelFlorb.MODEL_LOCATION);
		ModelLoader.setCustomModelResourceLocation(TEFlorbs.itemFlorb, 1, ModelFlorb.MAGMATIC_MODEL_LOCATION);

	}

	/* EVENT HANDLERS */
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		TETextures.registerIcons(event);
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return modelList.add(modelRegister);
	}

	private static ArrayList<IModelRegister> modelList = new ArrayList<IModelRegister>();

}
