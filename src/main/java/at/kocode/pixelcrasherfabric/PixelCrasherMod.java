package at.kocode.pixelcrasherfabric;

import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.client.packs.repository.PCPackRepository;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.file.Path;

public class PixelCrasherMod implements ModInitializer{
	public static final String MODID = "pixelcrasher";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static Path externalAssetSource;

	@Override
	public void onInitialize() {
		ClientPackSource clientpacksource = new ClientPackSource(externalAssetSource);
		RepositorySource repositorysource = new FolderRepositorySource(Minecraft.getInstance().getResourcePackDirectory(), PackType.CLIENT_RESOURCES, PackSource.DEFAULT);
		RepositorySource pcPackRepository = new PCPackRepository(PixelCrasher.getInstance(), PackType.CLIENT_RESOURCES, PackSource.BUILT_IN);

		try {
			Field resourcePackRepository = Minecraft.getInstance().getClass().getDeclaredField("resourcePackRepository");

			resourcePackRepository.setAccessible(true);
			resourcePackRepository.set(Minecraft.getInstance(), new PackRepository(clientpacksource, Minecraft.getInstance().getDownloadedPackSource(), repositorysource, pcPackRepository));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}
}