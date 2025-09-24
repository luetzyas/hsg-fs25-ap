import time
import torch
from torch import nn, optim, utils
from torchvision import datasets, transforms

# Load MNIST dataset (28x28 images of digits between 0 and 9)
preprocessing = transforms.Compose([transforms.ToTensor()])
train_xy = datasets.MNIST(root='./data', train=True, download=True, transform=preprocessing)
test_xy= datasets.MNIST(root='./data', train=False, download=True, transform=preprocessing)

# Create a simple classifier model
class Classifier(nn.Module):
    def __init__(self):
        super(Classifier, self).__init__()
        self.fc1 = nn.Linear(28*28, 16)
        self.fc2 = nn.Linear(16, 10)
        self.softmax = nn.Softmax(dim=1)
    
    def forward(self, x):
        x = torch.flatten(x, 1)
        x = self.fc1(x)
        x = self.fc2(x)
        x = self.softmax(x)
        return x

model = Classifier()

# Configure the training process
epochs = 20
batch_size = 128
optimizer = optim.Adam(model.parameters(), lr=0.01)
cross_entropy = nn.CrossEntropyLoss()
def loss(model, x, y):
    return cross_entropy(model(x), y)
def accuracy(model, test_set):
    with torch.no_grad():
        correct = 0
        total = 0
        for x, y in test_set:
            y_hat = torch.argmax(model(x), dim=1)
            correct += (y_hat == y).sum().item()
            total += y.size(0)
        return correct / total

training_set = utils.data.DataLoader(train_xy, batch_size=batch_size, shuffle=True)
test_set = utils.data.DataLoader(test_xy, batch_size=batch_size, shuffle=False)

# Evaluate the model (before training)
print(f'Accuracy (Before): {accuracy(model, test_set)}')

# Train the model
start = time.time()
for epoch in range(epochs):
    for iteration, (x, y) in enumerate(training_set, 0):
        optimizer.zero_grad()
        loss_i = loss(model, x, y)
        loss_i.backward()
        optimizer.step()
print(f'  {time.time() - start} seconds')

# Evaluate the model (after training)
print(f'Accuracy (After): {accuracy(model, test_set)}')