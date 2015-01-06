package org.scova.plugin.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//import org.scova.instrumenter.StateCoverageAsm;

import org.eclipse.core.filesystem.*;
import org.eclipse.ui.ide.*;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class RunHandler extends AbstractHandler {

	public static IProject getCurrentProject(IWorkbenchWindow workbenchWindow) {
		ISelectionService selectionService = workbenchWindow
				.getSelectionService();

		ISelection selection = selectionService.getSelection();

		IProject project = null;
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection)
					.getFirstElement();

			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
				// } else if (element instanceof PackageFragmentRootContainer) {
				// IJavaProject jProject =
				// ((PackageFragmentRootContainer)element).getJavaProject();
				// project = jProject.getProject();
				// }
				// else if (element instanceof IJavaElement) {
				// IJavaProject jProject=
				// ((IJavaElement)element).getJavaProject();
				// project = jProject.getProject();
				// }
			}
		}
		return project;
	}

	// public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	// try {
	// if (targetEditor != null && targetEditor.getEditorInput() != null) {
	// file = (IFile) targetEditor.getEditorInput().getAdapter(IFile.class);
	// ICompilationUnit unit = JavaCore.createCompilationUnitFrom(file);
	// this.javaName = unit.getTypes()[0].getFullyQualifiedName();
	// this.filePath = file.getLocation().toString();
	// this.javaProject = unit.getJavaProject();
	// }
	// } catch (JavaModelException e) {
	// ErrorHandler.reportError(e, getClass(), "setActiveEditor() error",
	// e.toString());
	// }
	// }
	//
	// private void doCompile() {
	// String compParam = null;
	// try {
	// String classPath = getClasspathInfo(this.javaProject);
	// String outputPath = getJavaOutputPath();
	// String filesToCompile = this.filePath;
	// if (filesToCompile.length() != 0) {
	// compParam = JavacCompiler.compileFile(classPath, filesToCompile,
	// outputPath);
	// HotSwap.hotSwap(this.javaName, getClassToHotswap(), getProjectName());
	// ErrorHandler
	// .openSuccessDialog("Compilation/Hotswap successfully finished",
	// "Following params used for compilation:\n\n" + compParam);
	//
	// }
	// } catch (Throwable e) {
	// ErrorHandler.reportError(e, getClass(), "Compilation error",
	// e.getMessage());
	// }
	// }

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = getCurrentProject(HandlerUtil
				.getActiveWorkbenchWindow(event));

		IJavaProject javaProject = JavaCore.create(project);

		String projectInputFolder = project.getLocation().toString();

		String projectOutputFolder = "C:/sc/" + project.getName();

		String projectInputClasspath = getClasspath(javaProject);

		IPath outputClassesLocation = null;
		try {
			outputClassesLocation = javaProject.getOutputLocation()
					.makeAbsolute();
			IFolder folder = project.getParent().getFolder(
					outputClassesLocation);
			outputClassesLocation = folder.getLocation();

			projectInputClasspath += outputClassesLocation.toString();

		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		IPath relative = outputClassesLocation.makeRelativeTo(project
				.getLocation());

		String projectOutputClasspath = projectOutputFolder + "/"
				+ relative.toString();

		// TODO: hardcoded
		String projectOutputTestFolder = "C:/sc/douglas+picon/src";

		try {
			executeInstrumentation(projectInputFolder, projectOutputFolder,
					projectInputClasspath, projectOutputClasspath,
					projectOutputTestFolder);
			
			executeReport(projectOutputFolder);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		

		// Process p;
		// try {
		// String [] command = { "C:/bin/apache-ant-1.8.4-bin/bin/ant.bat",
		// "instrument"};
		// p = Runtime.getRuntime().exec(command, null, scovaFolder);
		// int exitCode = p.waitFor();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
		// "Info", "Info for you");
		return null;
	}

	private void executeAnt(String... args) throws Exception {
		ProcessBuilder pb = new ProcessBuilder(args

		);
		pb.redirectError();

		File scovaFolder = new File(
				"c:/users/aniceto/workspace/scova_new/build");
		if (!scovaFolder.exists())
			throw new Exception("cant find scova path");

		// This should point to where your build.xml file is...
		pb.directory(scovaFolder);
		try {
			Process p = pb.start();
			InputStream is = p.getInputStream();
			int in = -1;
			while ((in = is.read()) != -1) {
				System.out.print((char) in);
			}
			int exitValue = p.waitFor();
			System.out.println("Exited with " + exitValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void executeReport(String outputFolder) throws Exception {
		executeAnt("C:/bin/apache-ant-1.8.4-bin/bin/ant.bat", "report", "-Dproject.output.folder=" + outputFolder);

		File fileToOpen = new File(outputFolder + "/report.html");

		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					fileToOpen.toURI());
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();

			try {
				IDE.openEditorOnFileStore(page, fileStore);
			} catch (PartInitException e) {
				// Put your exception handler here if you wish to
			}
		} else {
			// Do something if the file does not exist
		}

		//
		// String filePath = "..." ;
		// final IFile inputFile =
		// ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(filePath));
		// if (inputFile != null) {
		// IWorkbenchPage page =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//
		// IEditorDescriptor desc = PlatformUI.getWorkbench().
		// getEditorRegistry().getDefaultEditor(inputFile.getName());
		// page.openEditor(new FileEditorInput(inputFile), desc.getId());
		// // IEditorPart openEditor = IDE.openEditor(page, inputFile);
		// }

		// IWorkbenchPage page = ...;
		// IFile file = ...;
		// IEditorDescriptor desc = PlatformUI.getWorkbench().
		// getEditorRegistry().getDefaultEditor(file.getName());
		// page.openEditor(new FileEditorInput(file), desc.getId());
	}

	public String getClasspath(IJavaProject javaProject) {
		String projectClassPath = "";
		try {
			projectClassPath = getClasspathInfo(javaProject);
			System.out.println(projectClassPath);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projectClassPath.replace("\"", "");
	}
	
	private void executeFromLocalProject(String inputFolder, String outputFolder) {
		
//		StateCoverageAsm instrumenter = new StateCoverageAsm();
//		try {
//			instrumenter.instrumentFolder(inputFolder, outputFolder);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
	}

	private void executeInstrumentation(String projectInputFolder,
			String projectOutputFolder, String projectInputClasspath,
			String projectOutputClasspath, String projectOutputTestFolder)
			throws Exception {

		//executeFromLocalProject(projectInputFolder, projectOutputFolder);
		
		//return;
		executeAnt("C:/bin/apache-ant-1.8.4-bin/bin/ant.bat",
				"all", "-Dproject.input.folder="
						+ projectInputFolder, "-Dproject.output.folder="
						+ projectOutputFolder, "-Dproject.input.classpath="
						+ projectInputClasspath, "-Dproject.output.classpath="
						+ projectOutputClasspath, "-Dtest.home="
						+ projectOutputTestFolder);
	}

	private String getClasspathInfo(IJavaProject project) throws Exception {
		String result = "";
		try {
			IClasspathEntry[] entries = project.getResolvedClasspath(false);
			for (int i = 0; i < entries.length; i++) {
				if (entries[i].getContentKind() == IPackageFragmentRoot.K_BINARY
						&& entries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					result += convertEntryToSystemPath(project, entries[i]);
				} else if (entries[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
					IJavaProject referredProject = convertEntryToProject(entries[i]);
					result += getClasspathInfo(referredProject);
				}
			}
		} catch (JavaModelException e) {
			throw new Exception("Cannot setup classpath: " + e.toString());
		}
		return result;
	}

	private String convertEntryToSystemPath(IJavaProject project,
			IClasspathEntry entry) {
		String strPath = null;
		if (entry.getPath().toString().endsWith(".jar")
				|| entry.getPath().toString().endsWith(".zip")) {
			strPath = entry.getPath().toString();
		} else {
			IProject project2 = project.getProject();
			IPath tmpPath = entry.getPath().removeFirstSegments(1);
			IFolder folder = project2.getFolder(tmpPath);
			strPath = folder.getLocation().toString();
		}
		return "\"" + strPath + "\"" + ";";
	}

	private IJavaProject convertEntryToProject(IClasspathEntry entry) {
		String name = entry.getPath().toString().substring(1);
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(name);

		IJavaProject javaProject = JavaCore.create(project);
		return javaProject;
	}

	private String getJavaOutputPath(IJavaProject javaProject) throws Exception {
		String result = "";
		try {
			IPath outputLocation = javaProject.getOutputLocation();
			IProject project = javaProject.getProject();
			outputLocation = outputLocation.removeFirstSegments(1);
			IFolder folder = project.getFolder(outputLocation);
			result = folder.getLocation().toString();
		} catch (Throwable e) {
			throw new Exception("Cannot setup outputpath: " + e.toString());
		}
		return result;
	}

}
