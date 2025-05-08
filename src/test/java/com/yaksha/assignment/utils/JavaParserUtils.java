package com.yaksha.assignment.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.List;

public class JavaParserUtils {

	/**
	 * Loads the content of a class file from the given file path, parses it using
	 * JavaParser, and checks the class and method structure.
	 *
	 * @param filePath           Full path to the class file
	 *                           (e.g.,"src/main/java/com/yaksha/assignment/controller/AppController.java").
	 * @param classAnnotations   Annotations to check on the class
	 *                           (e.g., @RestController).
	 * @param methodName         The name of the method to inspect.
	 * @param methodAnnotations  Annotations to check on the method
	 *                           (e.g., @GetMapping).
	 * @param paramName          The name of the parameter to check for annotation
	 *                           (e.g., @RequestParam).
	 * @param expectedReturnType The expected return type of the method (e.g.,
	 *                           "String").
	 * @return boolean Returns true if all checks pass, false otherwise.
	 * @throws IOException
	 */
	public static boolean checkControllerStructure(String filePath, String classAnnotations, String methodName,
        String methodAnnotations, String paramName, String expectedReturnType) {

		System.out.println("Starting controller structure validation...");

		// Load class file as String from file path
		String classContent;
		try {
			classContent = loadClassContent(filePath);
			System.out.println("Successfully loaded class file from path: " + filePath);
		} catch (IOException e) {
			System.out.println("Error: Unable to read the class file from path: " + filePath);
			return false;
		}

		// Create a JavaParser instance and parse the class content
		JavaParser javaParser = new JavaParser();
		Optional<CompilationUnit> optionalCompilationUnit = javaParser.parse(classContent).getResult();

		// If parsing fails, return false
		if (optionalCompilationUnit.isEmpty()) {
			System.out.println("Error: Failed to parse the class content.");
			return false;
		}

		CompilationUnit compilationUnit = optionalCompilationUnit.get();
		System.out.println("Class file parsed successfully.");

		// Check if the class has the required annotations
		Optional<ClassOrInterfaceDeclaration> classOpt = compilationUnit.getClassByName("AppController");
		if (classOpt.isEmpty()) {
			System.out.println("Error: The class 'AppController' does not exist.");
			return false;
		}

		ClassOrInterfaceDeclaration classDecl = classOpt.get();
		boolean hasClassAnnotation = classDecl.getAnnotations().stream()
				.anyMatch(annotation -> annotation.getNameAsString().equals(classAnnotations));

		if (!hasClassAnnotation) {
			System.out.println("Error: The class is missing the @" + classAnnotations + " annotation. Please add it.");
			return false;
		} else {
			System.out.println("Success: The class contains the @" + classAnnotations + " annotation.");
		}

		// Check if the method exists and has the required annotations
		List<MethodDeclaration> methods = classDecl.getMethodsByName(methodName);
		if (methods.isEmpty()) {
			System.out.println("Error: The method '" + methodName + "' does not exist.");
			return false;
		}

		MethodDeclaration method = methods.get(0); // Safe from IndexOutOfBoundsException now

		boolean hasMethodAnnotation = method.getAnnotationByName(methodAnnotations).isPresent();
		if (!hasMethodAnnotation) {
			System.out.println("Error: The method " + methodName + " is missing the @" + methodAnnotations
					+ " annotation. Please add it.");
			return false;
		} else {
			System.out.println("Success: The method " + methodName + " contains the @" + methodAnnotations + " annotation.");
		}

		// Check if the method's parameter has the required annotation
		Optional<Parameter> paramOpt = method.getParameterByName(paramName);
		if (paramOpt.isEmpty()) {
			System.out.println("Error: The parameter '" + paramName + "' does not exist in the method.");
			return false;
		}

		Parameter param = paramOpt.get();
		boolean hasParamAnnotation = param.getAnnotationByName("RequestParam").isPresent();
		if (!hasParamAnnotation) {
			System.out.println("Error: The parameter " + paramName + " is missing the @RequestParam annotation. Please add it.");
			return false;
		} else {
			System.out.println("Success: The parameter " + paramName + " contains the @RequestParam annotation.");
		}

		// Check if the return type matches the expected type
		boolean isReturnTypeCorrect = method.getType().asString().equals(expectedReturnType);
		if (!isReturnTypeCorrect) {
			System.out.println("Error: The return type of the method " + methodName + " is not " + expectedReturnType
					+ ". Please correct it.");
			return false;
		} else {
			System.out.println("Success: The return type of the method " + methodName + " is correct.");
		}

		System.out.println("All checks passed successfully!");

		return true;
	}

	/**
	 * Load the content of a class from the file path and return it as a String.
	 *
	 * @param filePath Full path to the class file
	 *                 (e.g.,"src/main/java/com/yaksha/assignment/controller/AppController.java").
	 * @return The class content as a String.
	 * @throws IOException If an error occurs while reading the file.
	 */
	private static String loadClassContent(String filePath) throws IOException {
		// Create a File object from the provided file path
		File participantFile = new File(filePath);
		if (!participantFile.exists()) {
			throw new IOException("Class file not found: " + filePath);
		}

		// Read the content of the file
		try (FileInputStream fileInputStream = new FileInputStream(participantFile)) {
			byte[] bytes = fileInputStream.readAllBytes();
			return new String(bytes, StandardCharsets.UTF_8);
		}
	}
}
