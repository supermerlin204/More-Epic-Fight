package org.merlin204.mef.api.jar;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.merlin204.mef.main.MoreEpicFightMod;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

/**
 * 由于夕尘的preloading-tricks不能用常规的jar-in-jar打包,因此退而求其次采用复制方案
 * 负责将内嵌在 Mod JAR 中的多个依赖库复制到 Minecraft 的 mods 文件夹。
 * 所有需要复制的 jar 文件应放在 src/main/resources/embedded_jars/ 目录下，
 * 并在 JAR_FILES 列表中添加文件名。
 * 最后手段,未启用,找不到更好的方法再说
 */
public class EmbeddedJarCopier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MOD_ID = MoreEpicFightMod.MOD_ID;
    private static final String EMBEDDED_DIR = "/embedded_jars/";

    private static final List<String> JAR_FILES = Arrays.asList(
            "preloading-tricks-3.5.6.jar",
            "mef_filter-1.0.0.jar"
    );

    public static void copyJarsToMods() {
        if (!FMLLoader.isProduction()) {
            LOGGER.info("检测到开发环境，跳过内嵌 jar 复制。");
            return;
        }
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            LOGGER.info("检测到专用服务器，跳过内嵌 jar 复制。");
            return;
        }

        Path modsDir = FMLPaths.MODSDIR.get();
        try {
            Files.createDirectories(modsDir);
        } catch (IOException e) {
            LOGGER.error("无法创建 mods 目录: {}", e.getMessage());
            return;
        }

        for (String jarName : JAR_FILES) {
            Path targetFile = modsDir.resolve(jarName);
            if (Files.exists(targetFile)) {
                LOGGER.debug("{} 已存在于 mods 文件夹，跳过复制。", jarName);
                continue;
            }
            try {
                copySingleJar(jarName, targetFile);
                LOGGER.info("成功复制 {} 到 mods 文件夹。", jarName);
            } catch (Exception e) {
                LOGGER.error("复制 {} 失败: {}", jarName, e.getMessage());
            }
        }
    }

    private static void copySingleJar(String jarName, Path targetFile) throws IOException, URISyntaxException {
        String resourcePath = EMBEDDED_DIR + jarName;
        Class<?> modClass = ModList.get().getModObjectById(MOD_ID)
                .orElseThrow(() -> new RuntimeException("找不到 Mod: " + MOD_ID))
                .getClass();

        URL resourceUrl = modClass.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IOException("资源不存在: " + resourcePath);
        }

        if ("jar".equals(resourceUrl.getProtocol())) {
            copyFromJar(resourceUrl, targetFile);
        } else {
            Path sourcePath = Paths.get(resourceUrl.toURI());
            Files.copy(sourcePath, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void copyFromJar(URL resourceUrl, Path targetFile) throws IOException {
        String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        try (FileSystem jarFs = FileSystems.newFileSystem(Path.of(jarPath), (ClassLoader) null)) {
            Path sourceInJar = jarFs.getPath(resourceUrl.getPath().substring(resourceUrl.getPath().indexOf("!") + 1));
            Files.copy(sourceInJar, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
