# Gormott

An OpenGL rendering engine written in Java for displaying a user interface defined in HTML + CSS.

## Features

- **OpenGL Rendering Engine** based on LWJGL with GLFW support
- **HTML/CSS Parsing** custom parser to define the UI
- **2D UI Rendering** via custom shaders (vertex/fragment)
- **3D Rendering** with support for `.obj` models
- **Event Handling** (keyboard, window resizing)
- **Normalized Device Coordinates (NDC)** system for responsive rendering

## Architecture

```
src/
├── Gormott.java          # Entry point
├── DocumentObjModel.java # Document DOM model
├── HTMLParser.java       # Custom HTML parser
├── CSS.java              # CSS styling
├── HTMLNode.java         # DOM tree node
└── Elysium/
    ├── GlEngine.java     # Main graphics engine
    ├── UiRenderer.java   # UI rendering
    ├── Scene.java        # 3D scene
    ├── Mesh.java         # 3D model
    ├── MeshLoader.java   # .obj model loader
    ├── Element.java      # UI element
    ├── Input.java        # Input handling
    └── Utils.java        # Utilities
```

## Technologies

- **Java**
- **LWJGL** (Lightweight Java Game Library)
- **GLFW** (Windowing and input)
- **JOML** (OpenGL mathematics)
- **GLSL Shaders** (Vertex and Fragment)

## Getting Started

1. Ensure Java is installed
2. Add the dependencies in 'lib' to your build system
3. Run `Gormott.java`

## UI Structure

The interface is defined via HTML files with CSS for styling. UI elements are converted to colored rectangles rendered via OpenGL.