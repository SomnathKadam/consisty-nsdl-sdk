#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

/**
 * This script runs after the package is installed via npm/yarn
 * It attempts to add the required Bitbucket repository configuration to the host project
 */

try {
  // Try to find the root Android build.gradle file
  const possiblePaths = [
    // From node_modules
    path.resolve(process.cwd(), '..', '..', 'android', 'build.gradle'),
    // From project itself during development
    path.resolve(process.cwd(), 'example', 'android', 'build.gradle'),
  ];

  let androidBuildGradlePath = null;

  for (const p of possiblePaths) {
    if (fs.existsSync(p)) {
      androidBuildGradlePath = p;
      break;
    }
  }

  if (!androidBuildGradlePath) {
    console.log('Could not find Android build.gradle. Manual setup will be required.');
    process.exit(0);
  }

  // Read the current build.gradle file
  const buildGradleContent = fs.readFileSync(androidBuildGradlePath, 'utf8');

  // Check if the Bitbucket repository is already configured
  if (buildGradleContent.includes('api.bitbucket.org/2.0/repositories/perfios-android/SDK')) {
    console.log('Bitbucket repository already configured in build.gradle');
    process.exit(0);
  }

  // Load the template configuration
  const templatePath = path.resolve(__dirname, '..', 'android', 'project-build.gradle');
  
  if (!fs.existsSync(templatePath)) {
    console.log('Template build.gradle not found. Manual setup will be required.');
    process.exit(0);
  }
  
  const templateContent = fs.readFileSync(templatePath, 'utf8');

  // Create a backup of the original file
  fs.writeFileSync(`${androidBuildGradlePath}.bak`, buildGradleContent);

  // Add configuration to the build.gradle
  // This is a simple approach - in a real scenario, you might want to use a more robust solution
  // to insert at the right places rather than appending
  const updatedContent = buildGradleContent.replace(
    /buildscript\s*\{/,
    templateContent.split('buildscript {')[1].split('allprojects {')[0]
  );

  fs.writeFileSync(androidBuildGradlePath, updatedContent);

  console.log('Successfully configured Bitbucket repository in Android build.gradle');
} catch (error) {
  console.error('Error configuring Android build.gradle:', error.message);
  console.log('Please manually add the repository configuration as described in the README.md');
}