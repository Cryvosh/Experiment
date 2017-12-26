# Experiment

**General 3D Controls**

* Move mouse to look around
* W/A/S/D to move forward/left/back/right (local space)
* H/K, U/J, Y/I to move along x,y,z axes respectively (global space)
* Scroll up/down to speed up/slow down base movement speed
* Hold SHIFT/LEFT_CTRL to temporarily speed up/slow down
* Hold [/] to move the near clip plane closer/further

**General 2D Controls**

* W/A/S/D to move up/left/down/right
* Up/down arrows to zoom in/out

**Tup3D Controls**
* Press 8/9 to decrement/increment n by 1
* Hold 7/0 to decrement/increment n repeatedly

**Misc Controls**

* Press ESC to free mouse
* Press TAB to cycle through shaders
* Press R to reset/reload current shader
* Hold SPACE to show FPS
* Hold T to see shader time
* Hold LEFT_ALT to see integer camera coordinates

**Misc notes**

* All shader filenames should include one of '2d'/'2D'/'3d'/'3D' to denote 
which camera type/control scheme is required for that shader.

* Shaders can be modified during runtime by simply pressing R to reset/reload the current shader.

* General-purpose/Tup3D rendering can be toggled in Main.

* Only shaders in shaders/frag/general will be loaded by General.java.

* Tup3D.n can be set in code or read from binary file n_val/n.val, which can be modified using a 
hex editor.

* Y-axis is up (global space) by convention.