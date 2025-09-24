module GameOfLife
    Alive :: Bool = true
    Dead :: Bool = false

    function generate(width :: UInt16, height :: UInt16):: Matrix{Bool}
        # return fill(Dead, height, width)
        return rand(Bool, height, width)
    end

    function update(state :: Matrix{Bool}):: Matrix{Bool}
        new_state = copy(state)
        for i in 1:size(state, 1)
            for j in 1:size(state, 2)
                alive_neighbors = 0
                for x in max(1, i-1):min(size(state, 1), i+1)
                    for y in max(1, j-1):min(size(state, 2), j+1)
                        if (x != i || y != j) && state[x, y] == Alive
                            alive_neighbors += 1
                        end
                    end
                end
                if state[i, j] == Alive
                    if alive_neighbors < 2 || alive_neighbors > 3
                        new_state[i, j] = Dead
                    end
                else
                    if alive_neighbors == 3
                        new_state[i, j] = Alive
                    end
                end
            end
        end
        return new_state
    end

    function switch(state :: Matrix{Bool}, x :: Float64, y :: Float64):: Matrix{Bool}
        new_state = copy(state)
        i = Int(floor(y * size(state, 1))) + 1
        j = Int(floor(x * size(state, 2))) + 1
        new_state[i, j] = !state[i, j]
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
    state_height = UInt16(20),
    state_width = UInt16(20),
    gui_height_px = UInt16(600),
    gui_width_px = UInt16(600)
)