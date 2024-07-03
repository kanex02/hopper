using UnityEditor;
using UnityEditor.Build.Reporting;
using UnityEngine;

/// <summary>
/// Build script for building this project off the command line / terminal
/// </summary>
public class Builder
{
    /// <summary>
    /// Builds the target for the WebGL target
    /// </summary>
    private static void BuildWebGL()
    {   

        // Setup build options (e.g. scenes, build output location)
        var options = new BuildPlayerOptions
        {
            // Change to scenes from your project
            scenes = new[]
            {
                "Assets/Scenes/MainScene.unity",
            },
            // Change to location the output should go
            locationPathName = "../../src/main/resources/static/unity/",
            options = BuildOptions.None,
            target = BuildTarget.WebGL
        };

        var report = BuildPipeline.BuildPlayer(options);

        if (report.summary.result == BuildResult.Succeeded)
        {
            Debug.Log($"Build successful - Build written to {options.locationPathName}");
        }
        else if (report.summary.result == BuildResult.Failed)
        {
            Debug.LogError($"Build failed");
        }
    }

    /// <summary>
    /// This method is called from the build process in the gradle buildUnity task
    /// </summary>
    public static void Build()
    {
        // Build WebGL player
        BuildWebGL();
    }
}