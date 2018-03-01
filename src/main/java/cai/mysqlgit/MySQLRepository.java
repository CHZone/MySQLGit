package cai.mysqlgit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.annotations.NonNull;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.diff.DiffConfig;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FollowFilter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import cai.mysqlgit.utils.MySQLUtils;

public class MySQLRepository {
	private static Logger	logger	= Logger.getLogger(MySQLRepository.class);
	private String			basePathStr;
	private File			basePath;
	private Git				git;
	private Repository		mysqlGitRepository;

	public MySQLRepository() {
		basePathStr = MySQLGitConfig.getValue("mysql.repository.basepath").trim();
		basePath = new File(basePathStr);
	}

	public void init() {
		// 文件已存在怎么办
		// FileUtils.createDir(basePathStr);
		// 创建 Repository
		try {
			FileUtils.forceMkdir(basePath);
			git = Git.init().setDirectory(basePath).call();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void openRepository() {
		File repoDir = new File(basePathStr + "/.git");
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			mysqlGitRepository = builder.setGitDir(repoDir)
					.readEnvironment() // scan environment GIT_* variables
					.findGitDir() // scan up the file system tree
					.build();
			git = new Git(mysqlGitRepository);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addReadme() {
		File readme = new File(basePath + "/readme.md");
		try {
			readme.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			git.add()
					.addFilepattern("readme.md")
					.call();
			git.commit()
					.setMessage("Added readme.md")
					.call();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveTablesSQL(ArrayList<String> tableNameList, String databaseName) {
		for (String tableName : tableNameList) {
			String DBandTableDir = this.basePathStr + "/" + databaseName + "/" + tableName;
			// FileUtils.createDir(DBandTableDir);
			// org.apache.commons.io.FileUtils
			String tableSQLStr = MySQLUtils.getTableSql(databaseName, tableName);
			File sqlFile = new File(DBandTableDir + "/" + tableName + ".sql");
			try {
				FileUtils.writeStringToFile(sqlFile, tableSQLStr, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void saveDataBase(String databaseName) {
		// remove old path
		try {
			FileUtils.deleteDirectory(new File(this.basePathStr + "/" + databaseName));
			// get tables name
			ArrayList<String> tableNameList = MySQLUtils.getTablesByDatabaseName(databaseName);
			this.saveTablesSQL(tableNameList, databaseName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showStatus() {
		Status status;
		try {
			status = git.status().call();
			System.out.println("Added: " + status.getAdded());
			System.out.println("Changed: " + status.getChanged());
			System.out.println("Conflicting: " + status.getConflicting());
			System.out.println("ConflictingStageState: " + status.getConflictingStageState());
			System.out.println("IgnoredNotInIndex: " + status.getIgnoredNotInIndex());
			System.out.println("Missing: " + status.getMissing());
			System.out.println("Modified: " + status.getModified());
			System.out.println("Removed: " + status.getRemoved());
			System.out.println("Untracked: " + status.getUntracked());
			System.out.println("UntrackedFolders: " + status.getUntrackedFolders());
		} catch (NoWorkTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addAll() {
		try {
			git.add()
					.addFilepattern(".")
					.call();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reset() {
		try {
			git.reset()
					.setRef(null)
					.call();
		} catch (CheckoutConflictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void commit(String commitMessage) {
		try {
			git.commit()
					.setMessage(commitMessage)
					.call();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnmergedPathsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConcurrentRefUpdateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongRepositoryStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AbortedByHookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getLatest2Commits() {
		// get a list of all known heads, tags, remotes, ...
		Collection<Ref> allRefs = mysqlGitRepository.getAllRefs().values();
		// a RevWalk allows to walk over commits based on some filtering that is defined
		RevWalk revWalk = new RevWalk(mysqlGitRepository);
		for (Ref ref : allRefs) {
			try {
				revWalk.markStart(revWalk.parseCommit(ref.getObjectId()));
			} catch (MissingObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IncorrectObjectTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayList<String> latest2Commits = new ArrayList<>();
		int count = 0;
		for (RevCommit commit : revWalk) {
			latest2Commits.add(commit.getName());
			count++;
			if (count >= 2) {
				break;
			}
		}
		return latest2Commits;
	}

	public void walkAllCommits() {
		// get a list of all known heads, tags, remotes, ...
		Collection<Ref> allRefs = mysqlGitRepository.getAllRefs().values();
		// a RevWalk allows to walk over commits based on some filtering that is defined
		RevWalk revWalk = new RevWalk(mysqlGitRepository);
		for (Ref ref : allRefs) {
			try {
				revWalk.markStart(revWalk.parseCommit(ref.getObjectId()));
			} catch (MissingObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IncorrectObjectTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Walking all commits starting with " + allRefs.size() + " refs: " + allRefs);
		int count = 0;
		for (RevCommit commit : revWalk) {
			System.out.println("==========================================");
			System.out.println("Commit: " + commit);
			System.out.println("name: " + commit.name());
			System.out.println("id: " + commit.getId());
			System.out.println("type: " + commit.getType());
			System.out.println("commitTime: " + commit.getCommitTime());
			count++;
		}
		System.out.println("Had " + count + " commits");
	}

	public void diffFilesOf2Commits(String commitIdOld, String cimmitIdNew) {
		try {
			List<DiffEntry> diffs = git.diff()
					.setOldTree(prepareTreeParser(mysqlGitRepository, commitIdOld))
					.setNewTree(prepareTreeParser(mysqlGitRepository, cimmitIdNew))
					.call();
			System.out.println("Found: " + diffs.size() + " differences");
			for (DiffEntry diff : diffs) {
				System.out.println("Diff: " + diff.getChangeType() + ": " +
						(diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath() : diff.getOldPath() + " -> " + diff.getNewPath()));
			}
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
		// from the commit we can build the tree which allows us to construct the TreeParser
		// noinspection Duplicates
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit commit = walk.parseCommit(repository.resolve(objectId));
			RevTree tree = walk.parseTree(commit.getTree().getId());
			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}
			walk.dispose();
			return treeParser;
		}
	}

	public void fileInDiffOf2Commits(String commitIdOld, String cimmitIdNew, String filePath) {
		DiffEntry diff;
		try {
			diff = diffFile(mysqlGitRepository,
					commitIdOld,
					cimmitIdNew,
					filePath);
			// Display the diff
			// System.out.println("Showing diff of " + filePath);
			try (DiffFormatter formatter = new DiffFormatter(System.out)) {
				formatter.setRepository(mysqlGitRepository);
				// noinspection ConstantConditions
				formatter.format(diff);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static @NonNull DiffEntry diffFile(Repository repo, String oldCommit,
			String newCommit, String path) throws IOException, GitAPIException {
		Config config = new Config();
		config.setBoolean("diff", null, "renames", true);
		DiffConfig diffConfig = config.get(DiffConfig.KEY);
		try (Git git = new Git(repo)) {
			List<DiffEntry> diffList =
					git.diff().setOldTree(prepareTreeParser(repo, oldCommit)).setNewTree(prepareTreeParser(repo, newCommit)).setPathFilter(FollowFilter.create(path, diffConfig)).call();
			if (diffList.size() == 0)
				return null;
			if (diffList.size() > 1)
				throw new RuntimeException("invalid diff");
			return diffList.get(0);
		}
	}
}
