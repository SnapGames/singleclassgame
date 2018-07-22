/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.core.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.snapgames.game.singleclassgame.Game;

/**
 * This integrated class parse Maven model to expose a resulting list of
 * dependencies.
 *
 * @author Frédéric Delorme
 */
public class VersionTracker {
	private final Logger logger = LoggerFactory.getLogger(VersionTracker.class);
	Model model = null;

	public VersionTracker() {
		try {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			if (reader != null) {
				if ((new File("pom.xml")).exists()) {
					model = reader.read(new FileReader("pom.xml"));
				} else {
					model = reader.read(new InputStreamReader(Game.class
							.getResourceAsStream("/META-INF/maven/fr.snapgames.game/singleclassgame/pom.xml")));
				}
			}
		} catch (IOException | XmlPullParserException e) {
			logger.error("Unable to retrieve data from maven `pom.xml` file", e);
		}
		if (model == null) {
			logger.info("unable to read dependency data from maven");
		}
	}

	public void extractProjectInformation() {
		List<Dependency> deps = getDependencyList();
		logger.info("project name: " + getName());
		logger.info("project description: " + getDescription());
		logger.info("project version: " + getVersion());
		logger.info("dependency list:");
		int i = 0;
		for (Dependency dep : deps) {
			logger.info((i++) + " - " + dep.getType() + "://" + dep.getGroupId() + ":" + dep.getArtifactId() + ":"
					+ dep.getVersion());
		}
	}

	/**
	 * Parse pom.xml file to extract Maven dependencies for the project.
	 *
	 * @return List of corresponding dependencies.
	 * @see https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
	 */
	public List<Dependency> getDependencyList() {
		return (model != null ? model.getDependencies() : new ArrayList<Dependency>());
	}

	/**
	 * Retrive list of developers
	 *
	 * @return
	 */
	public List<Developer> getDevelopers() {
		return (model != null ? model.getDevelopers() : new ArrayList<Developer>());
	}

	/**
	 * Retrieve description
	 *
	 * @return
	 */
	public String getDescription() {
		return (model != null ? model.getDescription() : "");
	}

	/**
	 * Retrieve project name.
	 *
	 * @return
	 */
	public String getName() {
		return (model != null ? model.getName() : "");
	}

	/**
	 * Retrieve project version.
	 *
	 * @return
	 */
	public String getVersion() {
		return (model != null ? model.getVersion() : "");
	}

	/**
	 * Retrieve inception year
	 *
	 * @return
	 */
	public String getInceptionYear() {
		return (model != null ? model.getInceptionYear() : "");
	}

}