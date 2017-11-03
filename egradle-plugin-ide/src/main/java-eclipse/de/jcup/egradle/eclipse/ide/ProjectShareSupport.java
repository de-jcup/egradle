package de.jcup.egradle.eclipse.ide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;

public class ProjectShareSupport {
	

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
	 * @param monitor 
	 * 
	 * @param data
	 * @param projectsList
	 */
	public void reconnectToTeamProviders(IProgressMonitor monitor, ProjectShareData data, List<IProject> projectsList) {
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
			String projectName = project.getName();

			IPath path = project.getFullPath();
			String providerId = data.getProviderId(path);
			if (providerId == null) {
				continue;
			}
			try {
				RepositoryProvider.map(project, providerId);
				progressMessage(monitor, "Reconnected team provider for project: "+projectName);
			} catch (TeamException e) {
				IDEUtil.logError("Was not able to reconnect team provider on project:"+projectName, e);
			}
		}
	}
	
	private void progressMessage(IProgressMonitor monitor, String message) {
		if (monitor == null){
			return;
		}
		if (message==null){
			return;
		}
		monitor.subTask(message);
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
