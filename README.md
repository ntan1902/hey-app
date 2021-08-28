# Hey Pay - Online Chat and Payment Application

This is the probation project at VNG. Our project is a build-up from the [Hey project](https://github.com/zalopay-oss/hey-app), which is another probation project at VNG, it used React JS, Java Vert.x with Redis to simulate a simple chat application. We needed the best solutions for combining a project that was built a long time ago. In which, the design of the microservice model is one of the ideas that we highly recommend for our project.
In this project, we will use React JS, Java Spring Boot with MySQL to simulate a simple chat and payment application.

The project will have following functions:

- Sign In
- Sign Up
- Manage user's profile
- Chat with Other Person in friend list
- Chat in group
- Add new friend
- View Online/Offline friend along with their status
- Set Status
- Receive notification about Online/Offline friend in friend list
- Video call
- Share screen
- Topup
- Transfer money together
- Send lucky money to group chat
- Receive lucky money

To achieve these functionalities, we will use following technology stack:

- React JS with Redux for client side development
- Ant.design for UI/UX design
- Java Spring Boot, Vert.x for developing the API server
- WebSocket for messaging and instant notification
- MySQL, Redis as database

## Brief UIs of Hey Pay

![Hey Pay](https://github.com/zalopay-oss/hey-app/blob/master/presentation/image001.gif)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Install Maven, JDK Java 11
- Install MySQL 8
- Install Redis
- Install Node.js 16.4.2

Backend:

```
# You need to wait for every service finishing before starting next service.
./start-registry.sh
./start-auth.sh
./start-payment.sh
./start-chat.sh
./start-lucky.sh
./start-gateway.sh
```

Note: Please don't forget to start database MySQL and Redis before running any backend service

Frontend:

```
npm install
npm start
```

## Document

[Requirement](https://docs.google.com/document/d/187fRjvv_yQUhP8HRzvmx_B4pVWzxMcAV5NHx-ty-PU4/edit?usp=sharing)

[User Manual](https://docs.google.com/document/d/1oBx01YMy7EGyverJQ8M7pR-8aCJDrdS_NqDnEmAFtEc/edit?usp=sharing)

[API Specification](https://app.swaggerhub.com/apis-docs/oispmdp/BigAssignment/1.0#/)

## Support

- You are welcome to fork and submit pull requests.

## Authors

- **Võ Ngọc Trọng** - _VNG Fresher_
- **Nguyễn Trịnh An** - _VNG Fresher_
- **Lý Giới An** - _VNG Fresher_

## License

This project is licensed under the [MIT License](https://github.com/ntan1902/hey-app/blob/master/LICENSE)

## Acknowledgments

- Mr Đoàn Văn Việt - Senior Software Engineer - Promotions Technology
- Mr Nguyễn Văn Thử - Senior Software Engineer - Funding Technology
- Mr Lê Hoàng Đức Liêm - Lead Software Engineer - Consumer Technology
- Mr Làu Vòng Phát - Principal Software Engineer - Core & Data Platform
- Mr Lê Thái Phúc Quang - Senior Software Engineer - Core & Data Platform
- Mr Huỳnh Văn Hoàng - Senior Software Engineer - Core & Data Platform
