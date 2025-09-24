import random
import time

Alive = True
Dead = False
directions = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)]

def generate(width, height) -> list[list[bool]]:
    return [
        [Dead,  Alive, Dead,  Dead,  Dead,  Dead,  Dead,  Dead,  Dead,  Dead],
        [Dead,  Alive, Dead,  Dead,  Dead,  Dead,  Dead,  Alive, Alive, Alive],
        [Dead,  Alive, Dead,  Dead,  Dead,  Dead,  Dead,  Dead,  Dead,  Dead],
        [Dead,  Alive, Dead,  Alive, Dead,  Dead,  Dead,  Dead,  Alive, Alive],
        [Dead,  Dead,  Dead,  Dead,  Alive, Dead,  Dead,  Dead,  Alive, Alive],
        [Dead,  Dead,  Alive, Alive, Alive, Dead,  Dead,  Dead,  Dead,  Dead],
        [Dead,  Dead,  Dead,  Dead,  Dead,  Dead,  Dead,  Dead,  Dead,  Dead],
        [Dead,  Alive, Dead,  Dead,  Dead,  Dead,  Dead,  Alive, Dead,  Alive],
        [Alive, Dead,  Alive, Dead,  Dead,  Alive, Dead,  Dead,  Alive, Dead],
        [Dead,  Alive, Dead,  Dead,  Alive, Dead,  Alive, Dead,  Dead,  Dead]
    ]

def update(state) -> list[list[bool]]:
    def count_neighbors(state, x, y):
        count = 0
        for dx, dy in directions:
            nx, ny = x + dx, y + dy
            if 0 <= nx < len(state) and 0 <= ny < len(state[0]):
                count += state[nx][ny]
        return count

    new_state = [[Dead for _ in range(len(state[0]))] for _ in range(len(state))]
    for x in range(len(state)):
        for y in range(len(state[0])):
            neighbors = count_neighbors(state, x, y)
            if state[x][y] == Alive:
                if neighbors < 2 or neighbors > 3:
                    new_state[x][y] = Dead
                else:
                    new_state[x][y] = Alive
            else:
                if neighbors == 3:
                    new_state[x][y] = Alive
    return new_state

start = time.time()
iterations = 1000
state = generate(50, 50)
for i in range(iterations):
    state = update(state)
print(f"  {time.time() - start} seconds")