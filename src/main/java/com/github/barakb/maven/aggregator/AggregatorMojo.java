package com.github.barakb.maven.aggregator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.PatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Barak Bar Orion
 * on 5/29/16.
 *
 * @since 11.0
 */
@SuppressWarnings({"UnusedDeclaration"})
@Mojo(name = "aggregate", defaultPhase = LifecyclePhase.INSTALL,
        requiresDependencyResolution = ResolutionScope.TEST)
public class AggregatorMojo extends AbstractMojo {

    public AggregatorMojo() {
        super();
    }


    @Component
    private MavenProject project;

    @Parameter
    private List<String> includes;

    @Parameter
    private List<String> excludes;

    @SuppressWarnings("deprecation")
    @Component
    private ArtifactFactory factory;

    @Component
    private ArtifactResolver artifactResolver;

    @Component
    private ArchiverManager archiverManager;

    @Parameter(defaultValue = "javadoc-sources")
    private String toDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Artifact> artifacts = filter();
        List<Artifact> sources = new ArrayList<Artifact>();
        for (Artifact filtered : artifacts) {
            try {
                //noinspection deprecation
                ArtifactResult res = artifactResolver.resolveArtifact(project.getProjectBuildingRequest(), filtered);
                Artifact artifact = res.getArtifact();
                sources.add(artifact);
            } catch (ArtifactResolverException e) {
                getLog().warn("Failed to resolve source artifact " + filtered);
            }
        }

        File outDir = new File(project.getModel().getBuild().getOutputDirectory());

        for (Artifact source : sources) {
//            getLog().info("source jar to copy  " + source.getFile().getAbsolutePath());
            final File d = new File(outDir, toDir);
            if (!d.exists()) {
                //noinspection ResultOfMethodCallIgnored
                d.mkdirs();
            }
            final UnArchiver unArchiver;
            try {
                unArchiver = archiverManager.getUnArchiver(source.getType());
                unArchiver.setDestDirectory(d);
                unArchiver.setSourceFile(source.getFile());
                getLog().info("Unarchive " + source.getFile().getAbsolutePath() + " to " + d.getAbsolutePath());
                unArchiver.extract();
            } catch (NoSuchArchiverException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }

    private ZipArchiver createArchiver() throws MojoExecutionException {
        try {
            return (ZipArchiver) archiverManager.getArchiver("zip");
        } catch (NoSuchArchiverException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private List<Artifact> filter() {
        final AndArtifactFilter filter = new AndArtifactFilter();

        if (!getIncludes().isEmpty()) {
            final PatternIncludesArtifactFilter includeFilter =
                    new PatternIncludesArtifactFilter(getIncludes(), false);

            filter.add(includeFilter);
        }
        if (!getExcludes().isEmpty()) {
            final PatternExcludesArtifactFilter excludeFilter =
                    new PatternExcludesArtifactFilter(getExcludes(), false);

            filter.add(excludeFilter);
        }
        List<Artifact> res = new ArrayList<Artifact>();
        getLog().info("scanning artifacts");
        for (Artifact atf : project.getArtifacts()) {
//            getLog().info("scanning artifact " + atf);
            Artifact artifact = factory.createArtifactWithClassifier(atf.getGroupId(), atf.getArtifactId(), atf.getVersion(), atf.getType(), atf.getClassifier());
            if (filter.include(artifact)) {
                if ("jar".equals(artifact.getType())) {
                    Artifact source = factory.createArtifactWithClassifier(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), "sources");
                    if (source != null) {
                        getLog().info("--> adding source " + source);
                        res.add(source);
                    } else {
                        getLog().warn("--! source for artifact " + artifact + " not found");
                    }
                } else {
                    getLog().warn("--! ignoring none jar artifact" + artifact);
                }
            } else {
                getLog().info("--! artifact " + artifact + " was rejected by filter");
            }
        }
        return res;
    }

    private java.util.List<String> getExcludes() {
        if (this.excludes == null) {
            this.excludes = new java.util.ArrayList<String>();
        }

        return this.excludes;
    }

    private java.util.List<String> getIncludes() {
        if (this.includes == null) {
            this.includes = new java.util.ArrayList<String>();
        }

        return this.includes;
    }

}
