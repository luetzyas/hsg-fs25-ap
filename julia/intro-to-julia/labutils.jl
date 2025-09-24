module LabUtils
    using Test
    using Gtk4
    export start

    """
    Start a new game of life. The initial state is generated calling the 
    `generate` function with sizes `state_height` and `state_width`. The game is
    updated the state using the `update` function. The `switch` function is
    used to switch the state of a cell when the user clicks on it.
    """
    function start(;
        generate :: Function,
        update :: Function,
        switch :: Function,
        state_height :: UInt16,
        state_width :: UInt16,
        gui_height_px :: UInt16,
        gui_width_px :: UInt16
    )
        state = generate(state_width, state_height)
        canvas = GtkCanvas()
        window = GtkWindow(canvas, "Conway's Game of Life", gui_height_px, gui_width_px)
        mouse_controller = GtkGestureClick(window)
        keyboard_controller = GtkEventControllerKey(window)
        Gtk4.show(window)
        if !isinteractive()            
            @guarded draw(canvas) do widget
                LabUtils.draw_state(widget, state)
            end
            signal_connect(keyboard_controller, "key-released") do controller, keyval, keycode, keystate
                state = update(state)
                draw(canvas)
            end
            signal_connect(mouse_controller, "pressed") do controller, click_count, click_x, click_y
                canvas_height = Gtk4.height(canvas)
                canvas_width = Gtk4.width(canvas)
                x = click_x / canvas_width
                y = click_y / canvas_height
                state = switch(state, x, y)
                draw(canvas)
            end

            @async Gtk4.GLib.glib_main()
            Gtk4.GLib.waitforsignal(window,:close_request)
        end
    end

    """
    Draw the specified `state` on the given `canvas`.
    The `state` is a 2D boolean matrix where `false` represents a dead cell
    and `true` represents a live cell.
    """
    function draw_state(canvas, state :: Matrix{Bool})
        context = Gtk4.getgc(canvas)
        canvas_height = Gtk4.height(canvas)
        canvas_width = Gtk4.width(canvas)
        cell_height = canvas_height / size(state, 1)
        cell_width = canvas_width / size(state, 2)
        for i in 1:size(state, 1)
            for j in 1:size(state, 2)
                r, g, b = 1, 1, 1
                if state[i, j] r, g, b = 0, 0, 1 end
                Gtk4.rectangle(context, (j-1) * cell_width, (i-1) * cell_height, cell_width, cell_height)
                Gtk4.set_source_rgb(context, r, g, b)
                Gtk4.fill(context)
                Gtk4.rectangle(context, (j-1) * cell_width, (i-1) * cell_height, cell_width, cell_height)
                Gtk4.set_source_rgb(context, 0.95, 0.95, 0.95)
                Gtk4.stroke(context)
            end
        end
    end

    """
    Test the `generate`, `update`, and `switch` functions.
    """
    function tests(; generate, update, switch)      
        w = UInt16(10)
        h = UInt16(10)
        alive_or_dead(state) = state == 1 || state == 0
        state0 = Bool[
            0  1  0  0  0  0  0  0  0  0;
            0  1  0  0  0  0  0  1  1  1;
            0  1  0  0  0  0  0  0  0  0;
            0  1  0  1  0  0  0  0  1  1;
            0  0  0  0  1  0  0  0  1  1;
            0  0  1  1  1  0  0  0  0  0;
            0  0  0  0  0  0  0  0  0  0;
            0  1  0  0  0  0  0  1  0  1;
            1  0  1  0  0  1  0  0  1  0;
            0  1  0  0  1  0  1  0  0  0;
        ]
        state1 = Bool[
            0  0  0  0  0  0  0  0  1  0;
            1  1  1  0  0  0  0  0  1  0;
            1  1  0  0  0  0  0  1  0  0;
            0  0  1  0  0  0  0  0  1  1;
            0  0  0  0  1  0  0  0  1  1;
            0  0  0  1  1  0  0  0  0  0;
            0  0  1  1  0  0  0  0  0  0;
            0  1  0  0  0  0  0  0  1  0;
            1  0  1  0  0  1  1  1  1  0;
            0  1  0  0  0  1  0  0  0  0
        ]
        state2 = Bool[
            0  1  0  0  0  0  0  0  0  0;
            1  0  1  0  0  0  0  1  1  0;
            1  0  0  0  0  0  0  1  0  1;
            0  1  0  0  0  0  0  1  0  1;
            0  0  0  0  1  0  0  0  1  1;
            0  0  1  0  1  0  0  0  0  0;
            0  0  1  1  1  0  0  0  0  0;
            0  1  0  1  0  0  1  0  1  0;
            1  0  1  0  0  1  1  1  1  0;
            0  1  0  0  0  1  0  1  0  0
        ]
        stateA = Bool[0 1 0; 0 1 0; 0 1 0]
        stateB = Bool[0 1 0; 0 0 0; 0 1 0]
        stateC = Bool[1 1 0; 0 1 0; 0 1 0]

        try
            @testset verbose = true "generate" begin
                @test size(generate(w, h)) == (w, h)
                @test all(alive_or_dead, generate(w, h))
            end
        catch e
            println("Test failing for function `generate`")
            println(e)
        end
        
        try
            @testset verbose = true "update" begin
                @test update(state0) == state1
                @test update(state1) == state2
            end
        catch e
            println("Test failing for function `update`")
            println(e)
        end
        
        try
            @testset verbose = true "switch" begin
                @test switch(stateA, 0.5, 0.5) == stateB
                @test switch(stateA, 0.0, 0.0) == stateC
            end
        catch e
            println("Test failing for function `switch`")
            println(e)
        end
    end
end