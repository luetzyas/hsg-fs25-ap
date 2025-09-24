Alive :: Bool = true
Dead :: Bool = false

function generate(width :: UInt16, height :: UInt16):: Matrix{Bool}
    return Bool[
        Dead  Alive Dead  Dead  Dead  Dead  Dead  Dead  Dead  Dead;
        Dead  Alive Dead  Dead  Dead  Dead  Dead  Alive Alive Alive;
        Dead  Alive Dead  Dead  Dead  Dead  Dead  Dead  Dead  Dead;
        Dead  Alive Dead  Alive Dead  Dead  Dead  Dead  Alive Alive;
        Dead  Dead  Dead  Dead  Alive Dead  Dead  Dead  Alive Alive;
        Dead  Dead  Alive Alive Alive Dead  Dead  Dead  Dead  Dead;
        Dead  Dead  Dead  Dead  Dead  Dead  Dead  Dead  Dead  Dead;
        Dead  Alive Dead  Dead  Dead  Dead  Dead  Alive Dead  Alive;
        Alive Dead  Alive Dead  Dead  Alive Dead  Dead  Alive Dead;
        Dead  Alive Dead  Dead  Alive Dead  Alive Dead  Dead  Dead
    ]
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

@time begin
    iterations = 1000
    global state = generate(UInt16(50), UInt16(50))
    for i = 0:iterations
        global state = update(state)
    end
end