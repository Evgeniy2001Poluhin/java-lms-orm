#!/bin/bash

# Script to run the Learning Platform application

echo "🚀 Starting Learning Platform..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose down

# Build and start containers
echo "🔨 Building and starting containers..."
docker-compose up --build -d

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 30

# Check if application is running
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Application is running!"
    echo ""
    echo "📱 Access the application:"
    echo "   - API: http://localhost:8080"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - Database: localhost:5432"
    echo ""
    echo "📚 Demo credentials:"
    echo "   - Instructor: prof_ivanov / password123"
    echo "   - Student 1: student_petrov / password123"
    echo "   - Student 2: student_sidorova / password123"
    echo ""
    echo "📊 View logs: docker-compose logs -f app"
    echo "🛑 Stop application: docker-compose down"
else
    echo "❌ Application failed to start. Check logs: docker-compose logs app"
    exit 1
fi
