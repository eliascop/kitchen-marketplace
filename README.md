# 🍴 Kitchen Marketplace

## 🧾 Overview
**Kitchen Marketplace** is a modular platform designed for managing a kitchen-themed marketplace — from order placement and monitoring to payment and stock management.  
The project follows a distributed or microservice-like architecture, where each module handles a specific concern such as the web interface, backend services, or serverless order processing.

## 📦 Modules
| Module | Description |
|--------|--------------|
| `kitchen-web` | Web front-end for the marketplace (user interface). |
| `kitchen-backend` | Core backend exposing APIs and handling business logic. |
| `kitchen-order-monitoring` | Order monitoring service or dashboard that tracks order states. |
| `kitchen-order-preparing-simulator` | Order preparation simulator — helps test or demonstrate order workflows. |
| `order-lambda` | AWS Lambda function for processing orders. |
| `payment-lambda` | Function responsible for payment processing. |
| `stock-lambda` | Function for stock verification and inventory control. |
| `localstack` | Local AWS simulation environment using LocalStack. |
| `docker-compose.yml` | Docker Compose configuration to run all modules locally. |

> You can check the repo for additional modules such as `nginx` or infrastructure files.

## 🛠 Tech Stack
- **Languages:** Java (~59%) and TypeScript (~22%)  
- **Frontend:** HTML (~9%), CSS (~7%)  
- **Containerization:** Docker & Docker Compose  
- **Serverless:** AWS Lambda functions with local simulation with LocalStack
- **Proxy / Web Server:** Nginx  

## 🚀 Getting Started

### Prerequisites
- [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)  
- (Optional) AWS CLI credentials if you plan to connect to real services  
- Node.js & npm (for TypeScript/web modules)  
- Java + Maven/Gradle (for the backend)

### Run Everything with Docker Compose
```bash
git clone https://github.com/eliascop/kitchen-marketplace.git
cd kitchen-marketplace
docker-compose up --build
```

Then visit:
- Frontend: `http://localhost`  

### Run Individual Modules

**Backend:**
```bash
cd kitchen-backend
mvn spring-boot:run   # or ./gradlew bootRun
```

**Web Frontend:**
```bash
cd kitchen-web
npm install
npm run dev
```

**Lambdas (example):**
Depending on the stack, run locally or deploy using `serverless deploy`.

### Environment Variables
Example `.env` or `docker-compose.override.yml`:
```env
DATABASE_URL=...
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
PAYMENT_PROVIDER_API_KEY=...
```

## 💡 Features
- Kitchen-focused marketplace for products and services  
- End-to-end order management: creation, tracking, preparation, and payment  
- Preparation simulator for demo/testing environments  
- Serverless functions for order, payment, and stock management  
- Fully local environment powered by Docker + LocalStack  

## 🧩 Architecture & Data Flow
1. User places an order through the **web frontend**.  
2. The **backend API** handles order creation and sends data to the `order-lambda`.  
3. `payment-lambda` processes the transaction.  
4. `stock-lambda` checks and updates product availability.  
5. The **simulator** updates preparation progress.  
6. The **monitoring module** displays real-time order status (Received → Preparing → Ready → Delivered).

## 📅 Roadmap
- Authentication and user management  ✔️ 
- Admin panel for vendors  ✔️  
- Scalability improvements with service orchestration  (doing)
- Delivery integrations  ⏳
- Reporting and analytics  

## 📜 License
```
MIT License
```

## 🤝 Contributing
Contributions are welcome!

1. Fork the repository  
2. Create a new branch: `git checkout -b feature/my-feature`  
3. Commit changes: `git commit -m "Add new feature"`  
4. Push to your branch: `git push origin feature/my-feature`  
5. Open a Pull Request  

## 👤 Author
Developed by [Elias](https://github.com/eliascop).
