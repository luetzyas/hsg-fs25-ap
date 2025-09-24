# Install required packages
using Pkg
using Flux, MLDatasets, ProgressMeter

# Load MNIST dataset (28x28 images of digits between 0 and 9)
trainX, trainY = MLDatasets.MNIST(:train)[:]
testX, testY = MLDatasets.MNIST(:test)[:]
classes = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

# Preprocessing
trainX = Flux.reshape(trainX, 28*28, :)     # flattening:
testX = Flux.reshape(testX, 28*28, :)       # i.e. 28x28 -> 784x1
trainY = Flux.onehotbatch(trainY, classes)  # trasforms the labels to a probability distribution over the classes:
testY = Flux.onehotbatch(testY, classes)    # e.g. 3 -> [0, 0, 0, 1, 0, 0, 0, 0, 0, 0]

# Create a simple classifier model
function Classifier()
    return Flux.Chain(
        Flux.Dense(28*28 => 16),            # input layer:  784 (flattened image) -> hidden layer: 16 (features)
        Flux.Dense(16 => 10),               # hidden layer: 16  (features)        -> output layer: 10 (classes)
        Flux.softmax,                       # transform the output to a probability distribution over the classes
    )
end
model = Classifier()

# Configure the training process
# epochs: how many times the model sees the whole dataset
epochs = 20    
# batchsize: how many samples the models sees before updating its parameters
batchsize = 128                  
# optimizer: the algorithm that updates the model's parameters                   
optimizer = Flux.setup(Flux.Adam(0.01), model)   
# loss: the feedback given to the model for learning (how bad the model is performing)   
loss(model, x, y) = Flux.crossentropy(model(x), y)  
# accuracy: the percentage of data correctly classified (how well the model is performing)
accuracy(model, x, y) = Flux.mean(Flux.argmax(model(x); dims=1) .== Flux.argmax(y; dims=1))  

# Evaluate the model (before training)
accuracy_before = accuracy(model, testX, testY)
println("Accuracy (Before): $accuracy_before")

# Train the model
training_set = Flux.DataLoader((trainX, trainY), batchsize=batchsize, shuffle=true)
@time begin
    @showprogress for epoch in 1:epochs
        Flux.train!(loss, model, training_set, optimizer)
    end
end

# Evaluate the model (after training)
accuracy_after = accuracy(model, testX, testY)
println("Accuracy (After): $accuracy_after")