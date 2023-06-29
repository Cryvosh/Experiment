# Experiment

**3D Camera Controls**

* Move mouse to look around
* W/A/S/D to move forward/left/back/right (local space)
* H/K, U/J, Y/I to move along x,y,z axes respectively (global space)
* Scroll up/down to speed up/slow down base movement speed
* Hold SHIFT/LEFT_CTRL to temporarily speed up/slow down
* Hold [/] to move the near clip plane closer/further

**2D Camera Controls**

* W/A/S/D to move up/left/down/right
* Up/down arrows to zoom in/out

**apps/Tup3D Controls**

* Press 8/9 to decrement/increment n by 1
* Hold 7/0 to decrement/increment n repeatedly
* Set n via console using commands "/nFromFile" or "/nFromObj"

**apps/General Controls**
* Press TAB to cycle through shaders in shaders/frag/general

**Misc Controls**

* Press ESC to free mouse
* Press `/~/tilde to cycle through apps
* Hold LEFT_ALT to see integer camera coordinates
* Press R to reset/reload current shader
* Press E to do soft reset shader (camera stays put)
* Hold T to see shader time
* Hold SPACE to show FPS

**Misc notes**

* Tup3D.n can be set via commandline using commands `/nFromFile <path>` or `/nFromObj <path>` where `<path>` is optional, eg., `/nFromObj appData/Tup3D/n.obj`.

* All filenames in shaders/frag/general should include one of '2d'/'2D'/'3d'/'3D' to denote which camera type/control scheme is required for that shader.

* Shaders can be modified during runtime by simply pressing R to reset/reload the current shader.

* Y-axis is up (global space) by convention.