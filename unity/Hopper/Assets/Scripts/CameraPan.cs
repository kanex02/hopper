using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.InputSystem;

/// <summary>
/// Defines a camera panning monobehaviour for controlling the rotation of the camera
/// </summary>
public class CameraPan : MonoBehaviour
{

    /// <summary>
    /// The transform (position/rotation/scale) of the camera pivot. 
    /// This transform is rotated by the mouse, which makes the actual camera transform (which should be a child of the pivot)
    /// rotate like a third person camera.
    /// 
    /// This field is serializable and so it is set in the editor, rather than in the script
    /// </summary>
    [SerializeField]
    Transform cameraPivot;

    /// <summary>
    /// Controls the speed with which the camera is rotated
    /// 
    /// This field is serializable and so it is set in the editor, rather than in the script
    /// </summary>
    [SerializeField, Min(0f)]
    float cameraSpeed = 1.0f;

    /// <summary>
    /// Private flag that checks if the mouse is currently dragging
    /// </summary>
    private bool isDragging = false;
    
    /// <summary>
    /// A vector that records how far the mouse has moved in the last update
    /// </summary>
    private Vector3 mouseDelta = Vector3.zero;

    /// <summary>
    /// Update is called every frame 
    /// </summary>
    void Update()
    {
        if (isDragging)
        {
            cameraPivot.Rotate(mouseDelta * cameraSpeed);
        }
    }

    /// <summary>
    /// Event listener for when the mouse is clicked or screen is tapped
    /// </summary>
    /// <param name="context">Context object that records information about the action</param>
    public void OnClick(InputAction.CallbackContext context)
    {
        if (context.performed)
        {
            isDragging = true;
        }
        else if (context.canceled)
        {
            isDragging = false;
        }
    }

    /// <summary>
    /// Event listener for when the mouse is moved or the screen is "dragged"
    /// </summary>
    /// <param name="context">Context object that records information about the action</param>
    public void OnMoveMouse(InputAction.CallbackContext context)
    {
        if (isDragging && context.performed)
        {
            // records onto the y axis as that is the horizontal pivot axis in 3d
            mouseDelta.y = context.ReadValue<Vector2>().x;
        }
    }

}
