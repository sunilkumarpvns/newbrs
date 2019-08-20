package com.elitecore.core.commons.config.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;

public class FileMatchers {
	
		/**
		 * Returns a matcher which checks whether the file exists or not
		 * */
		public static ExistsMatcher exists() {
			return new ExistsMatcher();
		}
		
		/**
		 * Returns a matcher which checks whether the file contains the given line
		 * */
		public static ContainsMatcher containsLine(Matcher<String> lineMatcher) {
			return new ContainsMatcher(lineMatcher);
		}
		
		/**
		 * Returns a matcher which checks whether the file contains the given line at given line number
		 * */
		public static ContainsLineMatcher containsLine(int line, Matcher<String> lineMatcher) {
			return new ContainsLineMatcher(line, lineMatcher);
		}

	public static class ExistsMatcher extends TypeSafeDiagnosingMatcher<File> {

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("file exists");
		}

		@Override
		protected boolean matchesSafely(File arg0, Description arg1) {
			if (arg0.exists() == false) {
				arg1.appendValue(arg0.getAbsolutePath()).appendText(" does not exist");
				return false;
			}
			return true;
		}
	}

	public static class ContainsMatcher extends TypeSafeDiagnosingMatcher<File> {
		private Matcher<String> lineMatcher;

		public ContainsMatcher(Matcher<String> lineMatcher) {
			this.lineMatcher = lineMatcher;
		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("file to contain line").appendDescriptionOf(lineMatcher);
		}

		@Override
		protected boolean matchesSafely(File arg0, Description arg1) {
			ExistsMatcher exists = FileMatchers.exists();
			if (exists.matches(arg0) == false) {
				exists.describeMismatch(arg0, arg1);
				return false;
			}
			boolean matches = false;
			BufferedReader bufferedReader = null;
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(arg0);
				bufferedReader = new BufferedReader(fileReader);
				String line;
				while((line = bufferedReader.readLine()) != null) {
					if ((matches = lineMatcher.matches(line)))
						break;
				}
				if (matches == false) {
					arg1.appendText(" was not found");
				}
				return matches;
			} catch (FileNotFoundException e) {
				LogManager.getLogger().trace(e);
			} catch (IOException e) {
				LogManager.getLogger().trace(e);
			} finally {
				Closeables.closeQuietly(bufferedReader);
				Closeables.closeQuietly(fileReader);
			}

			return false;
		}

	}

	public static class ContainsLineMatcher extends TypeSafeDiagnosingMatcher<File> {
		private int lineNumber;
		private Matcher<String> lineMatcher;

		public ContainsLineMatcher(int line, Matcher<String> lineMatcher) {
			this.lineNumber = line;
			this.lineMatcher = lineMatcher;
		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("file to contain line ").appendValue(lineNumber)
			.appendText(" with value ").appendDescriptionOf(lineMatcher);
		}

		@Override
		protected boolean matchesSafely(File arg0, Description arg1) {
			if (FileMatchers.exists().matches(arg0) == false) {
				return false;
			}

			boolean matches = false;
			BufferedReader bufferedReader = null;
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(arg0);
				bufferedReader =  new BufferedReader(fileReader);
				int readLineNumber = 0;
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					readLineNumber++;
					if (lineNumber == readLineNumber) {
						matches = lineMatcher.matches(line);
						break;
					}
				}

				if (lineNumber > readLineNumber) {
					arg1.appendText("too few lines ").appendValue(readLineNumber);
					return false;
				}

				if (matches == false) {
					lineMatcher.describeMismatch(line, arg1);
					return false;
				}
				return true;
			} catch (IOException ex) {
				throw new AssertionError(ex);
			} finally {
				Closeables.closeQuietly(bufferedReader);
				Closeables.closeQuietly(fileReader);
			}
		}
	}
}
