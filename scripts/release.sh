#!/bin/bash
set -e

# SpeedPix Java SDK Release Script
# Usage: ./scripts/release.sh [snapshot|release] [version]

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [snapshot|release] [version]"
    echo ""
    echo "Examples:"
    echo "  $0 snapshot           # Deploy current snapshot version"
    echo "  $0 release 1.0.0      # Release version 1.0.0"
    echo ""
    echo "For first-time setup, see PUBLISHING.md"
}

# Check parameters
RELEASE_TYPE=$1
VERSION=$2

if [ -z "$RELEASE_TYPE" ]; then
    show_usage
    exit 1
fi

case $RELEASE_TYPE in
    "snapshot")
        print_status "Deploying snapshot version..."
        ;;
    "release")
        if [ -z "$VERSION" ]; then
            print_error "Version is required for release"
            show_usage
            exit 1
        fi

        # Validate version format (semantic versioning)
        if ! [[ $VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
            print_error "Version must follow semantic versioning format (e.g., 1.0.0)"
            exit 1
        fi

        print_status "Starting release process for version $VERSION"
        ;;
    *)
        print_error "Invalid release type: $RELEASE_TYPE"
        show_usage
        exit 1
        ;;
esac

# Pre-flight checks
print_status "Running pre-flight checks..."

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    print_error "pom.xml not found. Please run this script from the project root."
    exit 1
fi

# Check for uncommitted changes (only for release)
if [ "$RELEASE_TYPE" = "release" ] && [ -n "$(git status --porcelain)" ]; then
    print_error "You have uncommitted changes. Please commit or stash them first."
    git status --short
    exit 1
fi

# Check if GPG key is available
if ! gpg --list-secret-keys | grep -q "sec"; then
    print_error "No GPG secret key found. Please set up GPG signing first."
    print_error "See PUBLISHING.md for setup instructions."
    exit 1
fi

# Check Maven settings for OSSRH credentials
if [ ! -f ~/.m2/settings.xml ]; then
    print_warning "Maven settings.xml not found."
    print_warning "Make sure SONATYPE_USERNAME and SONATYPE_PASSWORD environment variables are set."
fi

print_status "Pre-flight checks passed"

# Run tests
print_status "Running tests..."
mvn clean test

if [ $? -ne 0 ]; then
    print_error "Tests failed. Release cancelled."
    exit 1
fi

print_status "All tests passed"

if [ "$RELEASE_TYPE" = "release" ]; then
    # Release workflow
    CURRENT_BRANCH=$(git branch --show-current)

    # Check if we're on main branch
    if [ "$CURRENT_BRANCH" != "main" ]; then
        print_warning "You're not on the main branch. Current branch: $CURRENT_BRANCH"
        read -p "Do you want to continue? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_error "Release cancelled"
            exit 1
        fi
    fi

    # Check if tag already exists
    if git tag -l | grep -q "v$VERSION"; then
        print_error "Tag v$VERSION already exists"
        exit 1
    fi

    # Update version in pom.xml
    print_status "Updating version to $VERSION"
    mvn versions:set -DnewVersion=$VERSION -DgenerateBackupPoms=false

    # Commit version change
    print_status "Committing version change..."
    git add pom.xml
    git commit -m "Release version $VERSION"

    # Create and push tag
    print_status "Creating tag v$VERSION..."
    git tag -a "v$VERSION" -m "Release version $VERSION"

    print_status "Pushing changes and tag..."
    git push origin $CURRENT_BRANCH
    git push origin "v$VERSION"

    # Deploy to Maven Central
    print_status "Deploying to Maven Central..."
    mvn clean deploy -P ossrh

    if [ $? -eq 0 ]; then
        print_status "Deployment successful!"
        print_status "Artifact: io.github.speedpix:speedpix-java:$VERSION"
        print_status ""
        print_status "Next steps:"
        echo "1. Check https://central.sonatype.com/ for the artifact"
        echo "2. The artifact will be available in Maven Central within 30 minutes"
        echo "3. Update documentation if needed"
    else
        print_error "Deployment failed!"
        exit 1
    fi

    # Prepare next development version
    NEXT_VERSION=$(echo $VERSION | awk -F. '{$NF = $NF + 1; print}' | sed 's/ /./g')
    NEXT_SNAPSHOT="$NEXT_VERSION-SNAPSHOT"

    print_status "Preparing next development version: $NEXT_SNAPSHOT"
    mvn versions:set -DnewVersion=$NEXT_SNAPSHOT -DgenerateBackupPoms=false
    git add pom.xml
    git commit -m "Prepare for next development iteration"
    git push origin $CURRENT_BRANCH

    print_status "Release process completed successfully!"
    print_status "Released version: $VERSION"
    print_status "Next development version: $NEXT_SNAPSHOT"

else
    # Snapshot workflow
    print_status "Deploying snapshot version..."
    mvn clean deploy -P ossrh

    if [ $? -eq 0 ]; then
        print_status "Snapshot deployment successful!"
        print_status "Snapshot artifacts are available in OSSRH snapshot repository"
    else
        print_error "Snapshot deployment failed!"
        exit 1
    fi
fi
