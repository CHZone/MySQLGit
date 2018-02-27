package cai.mysqlgit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
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
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import cai.mysqlgit.utils.FileUtils;
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
//		init();
	}

	public void init() {
		// 文件已存在怎么办
		FileUtils.createDir(basePathStr);
		// 创建 Repository
		try {
			git = Git.init().setDirectory(basePath).call();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
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
		// System.out.println("Having repository: " + mysqlGitRepository.getDirectory());
		// // the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
		// Ref head = null;
		// try {
		// head = mysqlGitRepository.exactRef("refs/heads/master");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("Ref of refs/heads/master: " + head);
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
			FileUtils.createDir(DBandTableDir);
			String tableSQLStr = MySQLUtils.getTableSql(databaseName, tableName);
			FileUtils.saveTableSqlFile(DBandTableDir + "/" + tableName + ".sql", tableSQLStr);
		}
	}

	public void saveDataBase(String databaseName) {
		// remove old path
		FileUtils.deleteDir(this.basePathStr + "/" + databaseName);
		// get tables name
		ArrayList<String> tableNameList = MySQLUtils.getTablesByDatabaseName(databaseName);
		this.saveTablesSQL(tableNameList, databaseName);
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
	
	public void diff() {
		
	}
}
