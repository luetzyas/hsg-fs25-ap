module GameOfLife
    Alive :: Bool = true
    Dead :: Bool = false

    function generate(width :: UInt16, height :: UInt16):: Matrix{Bool}
        return rand(Bool, height, width)
    end

    function update(state :: Matrix{Bool}):: Matrix{Bool}
        height, width = size(state)
        new_state = similar(state)
        for y in 1:height
            for x in 1:width
                # Count alive neighbors
                alive_neighbors = 0
                for dy in -1:1
                    for dx in -1:1
                        if (dy != 0 || dx != 0) # Exclude the cell itself
                            ny = mod1(y + dy, height) # Wrap around vertically
                            nx = mod1(x + dx, width)  # Wrap around horizontally
                            if state[ny, nx] == Alive
                                alive_neighbors += 1
                            end
                        end
                    end
                end

                # Apply the rules of Conway's Game of Life
                if state[y, x] == Alive
                    if alive_neighbors < 2 || alive_neighbors > 3
                        new_state[y, x] = Dead # Underpopulation or overpopulation
                    else
                        new_state[y, x] = Alive # Lives on to the next generation
                    end
                else # Cell is Dead
                    if alive_neighbors == 3
                        new_state[y, x] = Alive # Reproduction
                    else
                        new_state[y, x] = Dead # Remains dead
                    end
                end
            end
        end
        return new_state
    end

    function switch(state :: Matrix{Bool}, x :: Float64, y :: Float64):: Matrix{Bool}
        # Switch the state of the cell that is in the position (x,y) where x and y are given as relative coordinates in the matrix
        height, width = size(state)
        ix = clamp(Int(floor(x * width)) + 1, 1, width)
        iy = clamp(Int(floor(y * height)) + 1, 1, height)
        new_state = copy(state)
        new_state[iy, ix] = !state[iy, ix]
        return new_state
    end
end

include("labutils.jl"); 
using .LabUtils
LabUtils.tests(
    generate = GameOfLife.generate,
    update = GameOfLife.update,
    switch = GameOfLife.switch
)
LabUtils.start(
    generate = GameOfLife.generate,
    update = GameOfLife.update,
    switch = GameOfLife.switch,
    state_height = UInt16(50),
    state_width = UInt16(50),
    gui_height_px = UInt16(600),
    gui_width_px = UInt16(600)
)