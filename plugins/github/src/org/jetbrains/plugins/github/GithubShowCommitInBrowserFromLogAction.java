/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.github;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.VcsLog;
import com.intellij.vcs.log.VcsLogDataKeys;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.util.GithubUtil;

import java.util.List;

public class GithubShowCommitInBrowserFromLogAction extends GithubShowCommitInBrowserAction {

  @Override
  public void update(@NotNull AnActionEvent e) {
    Project project = e.getData(CommonDataKeys.PROJECT);
    VcsLog log = e.getData(VcsLogDataKeys.VCS_LOG);
    if (project == null || log == null) {
      e.getPresentation().setEnabledAndVisible(false);
      return;
    }
    List<VcsFullCommitDetails> commits = log.getSelectedDetails();
    if (commits.size() != 1) {
      e.getPresentation().setEnabledAndVisible(false);
      return;
    }
    GitRepository repository = GitUtil.getRepositoryManager(project).getRepositoryForRoot(commits.get(0).getRoot());
    e.getPresentation().setEnabledAndVisible(repository != null && GithubUtil.isRepositoryOnGitHub(repository));
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getRequiredData(CommonDataKeys.PROJECT);
    VcsFullCommitDetails commit = e.getRequiredData(VcsLogDataKeys.VCS_LOG).getSelectedDetails().get(0);
    GitRepository repository = GitUtil.getRepositoryManager(project).getRepositoryForRoot(commit.getRoot());
    openInBrowser(project, repository, commit.getId().asString());
  }

}
