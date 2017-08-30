/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details. You should have received a copy of the GNU Lesser General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 * 
 *******************************************************************************/
package es.indaba.sqld.impl.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

import es.indaba.sqld.QueryDefinitionsStaticHolder;
import es.indaba.sqld.api.QueryDefinitionRepository;
import es.indaba.sqld.impl.parser.TextBlockReader;
import es.indaba.sqld.impl.parser.YamlFileReader;

public final class QueryDefinitionClassPathLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryDefinitionClassPathLoader.class);

  
//    public static void loadSqlds(final String prefix) {
//        loadBlockFiles(prefix, "sqld");
//        loadYamlFiles(prefix, "ysqld");
//    }

    /**
     * Loads text block files with sqld extension. Each block is loaded into the QueryDefinitionsManager.
     * 
     * @param prefix - is the package prefix where the sqld files are located.
     */
    public static void loadSqlds(final String prefix, QueryDefinitionRepository repository) {
        loadBlockFiles(prefix, "sqld", repository);
        loadYamlFiles(prefix, "ysqld", repository);
    }
    

    /**
     * Loads text block files with the specified extension. Each block is loaded into the QueryDefinitionsManager.
     * 
     * @param prefix - is the package prefix where the sqld files are located.
     * 
     * @param extension - is the file extension
     */
//    public static void loadBlockFiles(final String prefix, final String extension) {
//        final Predicate<String> filter = new FilterBuilder().include(prefix + ".*\\." + extension);
//        final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(filter)
//                .setScanners(new ResourcesScanner()).setUrls(ClasspathHelper.forClassLoader()));
//        final Set<String> resources = reflections.getResources(Pattern.compile(".*\\." + extension));
//        for (final String resource : resources) {
//            try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
//                LOGGER.debug("Loading SQL file {} ", resource);
//                QueryDefinitionsStaticHolder.loadTextBlockFile(stream, resource);
//            } catch (final IOException e) {
//                LOGGER.error("Error Loading SQL file {} ", resource, e);
//            }
//        }
//    }

    /**
     * Loads text block files with the specified extension. Each block is loaded into the QueryDefinitionsManager.
     * 
     * @param prefix - is the package prefix where the sqld files are located.
     * 
     * @param extension - is the file extension
     */
    public static void loadBlockFiles(final String prefix, final String extension,
            QueryDefinitionRepository repostory) {
        final Predicate<String> filter = new FilterBuilder().include(prefix + ".*\\." + extension);
        final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(filter)
                .setScanners(new ResourcesScanner()).setUrls(ClasspathHelper.forClassLoader()));
        final Set<String> resources = reflections.getResources(Pattern.compile(".*\\." + extension));
        for (final String resource : resources) {
            try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
                LOGGER.debug("Loading SQL file {} ", resource);
                loadTextBlockFile(stream, resource, repostory);
            } catch (final IOException e) {
                LOGGER.error("Error Loading SQL file {} ", resource, e);
            }
        }
    }

    private static synchronized void loadTextBlockFile(final InputStream aInput, final String aSqlFileName,
            QueryDefinitionRepository repository) throws IOException {
        if (repository.isFileProcessed(aSqlFileName)) {
            LOGGER.debug("The file '{}' is already loaded.", aSqlFileName);
            return;
        }
        final TextBlockReader sqlReader = new TextBlockReader(aInput, aSqlFileName);
        final Properties qFile = sqlReader.read();
        repository.addQuery(qFile, aSqlFileName);
        repository.fileLoaded(aSqlFileName);
    }

//    public static void loadYamlFiles(final String prefix, final String extension) {
//        final Predicate<String> filter = new FilterBuilder().include(prefix + ".*\\." + extension);
//        final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(filter)
//                .setScanners(new ResourcesScanner()).setUrls(ClasspathHelper.forClassLoader()));
//        final Set<String> resources = reflections.getResources(Pattern.compile(".*\\." + extension));
//        for (final String resource : resources) {
//            try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
//                LOGGER.debug("Loading SQL Yaml file {} ", resource);
//                QueryDefinitionsStaticHolder.loadYamlFile(stream, resource);
//            } catch (final IOException e) {
//                LOGGER.error("Error Loading SQL Yaml file {} ", resource, e);
//            }
//        }
//    }

    public static void loadYamlFiles(final String prefix, final String extension,
            QueryDefinitionRepository repository) {
        final Predicate<String> filter = new FilterBuilder().include(prefix + ".*\\." + extension);
        final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(filter)
                .setScanners(new ResourcesScanner()).setUrls(ClasspathHelper.forClassLoader()));
        final Set<String> resources = reflections.getResources(Pattern.compile(".*\\." + extension));
        for (final String resource : resources) {
            try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
                LOGGER.debug("Loading SQL Yaml file {} ", resource);
                loadYamlFile(stream, resource, repository);
            } catch (final IOException e) {
                LOGGER.error("Error Loading SQL Yaml file {} ", resource, e);
            }
        }
    }

    public static void loadYamlFile(final InputStream aInput, final String aSqlFileName,
            QueryDefinitionRepository repository) throws IOException {
        if (repository.isFileProcessed(aSqlFileName)) {
            LOGGER.debug("The file '{}' is already loaded.", aSqlFileName);
            return;
        }

        final YamlFileReader sqlReader = new YamlFileReader(aInput, aSqlFileName);
        final Properties qFile = sqlReader.read();
        repository.addQuery(qFile, aSqlFileName);
        repository.fileLoaded(aSqlFileName);
    }


    private QueryDefinitionClassPathLoader() {
        // Avoid instances of this Utility Class
    }
}
