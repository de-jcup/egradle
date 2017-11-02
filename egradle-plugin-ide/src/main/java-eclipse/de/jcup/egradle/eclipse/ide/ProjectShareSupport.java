package de.jcup.egradle.eclipse.ide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;
import org.junit.FixMethodOrder;

public class ProjectShareSupport {
	
	private static boolean TURNED_OFF=true;


	public ProjectShareData resolveProjectShareDataForProjects(List<IProject> projects) {
		ProjectShareData data = new ProjectShareData();
		
		for (IProject project: projects){
			RepositoryProvider provider = RepositoryProvider.getProvider(project);
			if (provider == null) {
				continue;
			}
			data.register(provider, project);
		}
		return data;
	}

	/**
	 * Will try to reconnect team provicers - but only for projects not being
	 * shared already!
	 * 
	 * @param data
	 * @param projectsList
	 */
	public void reconnectToTeamProviders(ProjectShareData data, List<IProject> projectsList) {
		if (TURNED_OFF){
			/* FIXME ATR, 02.11.2017: this is currently only for debugging. maybe this could be a configuration in
			 * reimport in future - but currently we can simulate the old behaviour, because it seems the reconnect
			 * does appear normally automatically (being correct).
			 */
			return;
		}
		if (data == null) {
			return;
		}
		if (projectsList == null || projectsList.isEmpty()) {
			return;
		}

		for (IProject project : projectsList) {
			if (RepositoryProvider.isShared(project)) {
				/* already shared - so ignore! */
				continue;
			}
			IPath path = project.getFullPath();
			String providerId = data.getProviderId(path);
			if (providerId == null) {
				continue;
			}
			try {
				RepositoryProvider.map(project, providerId);
			} catch (TeamException e) {
				IDEUtil.logError("Was not able to reconnect team provider on project:"+project.getName(), e);
			}
		}
	}

	public class ProjectShareData {
		private Map<IPath, String> map = new HashMap<>();

		public void register(RepositoryProvider provider, IProject project) {
			if (provider == null) {
				return;
			}
			if (project==null){
				return;
			}
			IPath fullPath = project.getFullPath();
			if (fullPath == null) {
				return;
			}
			map.put(fullPath, provider.getID());

		}

		public String getProviderId(IPath path) {
			return map.get(path);
		}

	}
}
