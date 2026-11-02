-- ============================================================
-- PLACEMENT MANAGEMENT SYSTEM - MYSQL DATABASE SCRIPT
-- Database Name: krishna
-- ============================================================

CREATE DATABASE IF NOT EXISTS krishna;
USE krishna;

-- 1. USERS TABLE (Admin Login Credentials)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN'
);

INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE password = 'admin123';

-- 2. STUDENTS TABLE
CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    branch VARCHAR(50) NOT NULL,
    cgpa VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    company VARCHAR(100) DEFAULT '-'
);

-- 3. COMPANIES TABLE (Placement Drives)
CREATE TABLE IF NOT EXISTS companies (
    name VARCHAR(100) PRIMARY KEY,
    package VARCHAR(50) NOT NULL,
    role VARCHAR(100) NOT NULL,
    date VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- 4. NOTICES TABLE (Notice Board)
CREATE TABLE IF NOT EXISTS notices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    date VARCHAR(50) NOT NULL
);

-- VERIFY
SELECT * FROM users;
SELECT * FROM students;
SELECT * FROM companies;
SELECT * FROM notices;
