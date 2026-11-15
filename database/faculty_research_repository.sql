-- =====================================================================
-- DATABASE SETUP SCRIPT FOR FACULTY RESEARCH PUBLICATION REPOSITORY
-- Designed for MySQL 8.0+
-- Database Name: faculty_research_repository
-- =====================================================================

-- Step 1: Create Database if not exists
CREATE DATABASE IF NOT EXISTS faculty_research_repository;
USE faculty_research_repository;

-- Step 2: Drop existing tables in reverse dependency order (if re-running)
DROP TABLE IF EXISTS publications;
DROP TABLE IF EXISTS faculty;
DROP TABLE IF EXISTS users;

-- =====================================================================
-- TABLE 1: users
-- Stores authentication and role-based access information
-- Roles: 'Admin', 'User'
-- =====================================================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('Admin', 'User') NOT NULL DEFAULT 'User',
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- TABLE 2: faculty
-- Stores faculty/author profiles
-- =====================================================================
CREATE TABLE faculty (
    faculty_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(30) NOT NULL UNIQUE,
    faculty_name VARCHAR(100) NOT NULL,
    department VARCHAR(80) NOT NULL,
    designation VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    specialization VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- TABLE 3: publications
-- Stores publication records linked to faculty
-- Foreign Key: faculty_id -> faculty(faculty_id) with CASCADE DELETE
-- =====================================================================
CREATE TABLE publications (
    publication_id INT AUTO_INCREMENT PRIMARY KEY,
    faculty_id INT NOT NULL,
    title VARCHAR(300) NOT NULL,
    publication_type ENUM('Journal', 'Conference', 'Book Chapter', 'Patent', 'Workshop', 'Preprint') NOT NULL DEFAULT 'Journal',
    journal_conference VARCHAR(200) NOT NULL,
    research_area VARCHAR(100) NOT NULL,
    publication_year INT NOT NULL,
    doi VARCHAR(100),
    issn_isbn VARCHAR(50),
    publisher VARCHAR(100),
    publication_status ENUM('Published', 'Accepted', 'Under Review', 'Submitted') NOT NULL DEFAULT 'Published',
    publication_link VARCHAR(255),
    abstract TEXT,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_publication_faculty
        FOREIGN KEY (faculty_id)
        REFERENCES faculty(faculty_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- SAMPLE DATA INSERTIONS
-- =====================================================================

-- 1. Sample Users (Default Admin and Faculty Users)
-- Admin Account: admin / admin123
-- User Accounts: user123 password for all sample users
INSERT INTO users (username, password, role, full_name, email) VALUES
('admin', 'admin123', 'Admin', 'System Administrator', 'admin@university.edu'),
('john_doe', 'user123', 'User', 'Dr. John Doe', 'john.doe@university.edu'),
('prof_smith', 'user123', 'User', 'Prof. Alice Smith', 'alice.smith@university.edu'),
('anita_roy', 'user123', 'User', 'Dr. Anita Roy', 'anita.roy@university.edu'),
('raj_sharma', 'user123', 'User', 'Dr. Rajesh Sharma', 'rajesh.sharma@university.edu');

-- 2. Sample Faculty Members
INSERT INTO faculty (employee_id, faculty_name, department, designation, email, phone, specialization) VALUES
('FAC-CSE-001', 'Dr. John Doe', 'Computer Science & Engineering', 'Professor & HOD', 'john.doe@university.edu', '+91 9876543210', 'Artificial Intelligence, Machine Learning'),
('FAC-CSE-002', 'Prof. Alice Smith', 'Computer Science & Engineering', 'Associate Professor', 'alice.smith@university.edu', '+91 9876543211', 'Distributed Systems, Cloud Computing'),
('FAC-IT-003', 'Dr. Anita Roy', 'Information Technology', 'Professor', 'anita.roy@university.edu', '+91 9876543212', 'Cyber Security, Blockchain Technology'),
('FAC-ECE-004', 'Dr. Rajesh Sharma', 'Electronics & Communication', 'Assistant Professor', 'rajesh.sharma@university.edu', '+91 9876543213', 'VLSI Design, Internet of Things (IoT)'),
('FAC-ME-005', 'Prof. David Miller', 'Mechanical Engineering', 'Professor', 'david.miller@university.edu', '+91 9876543214', 'Thermal Engineering, Robotics & Automation'),
('FAC-IT-006', 'Dr. Priya Patel', 'Information Technology', 'Assistant Professor', 'priya.patel@university.edu', '+91 9876543215', 'Big Data Analytics, NLP');

-- 3. Sample Publications
INSERT INTO publications (faculty_id, title, publication_type, journal_conference, research_area, publication_year, doi, issn_isbn, publisher, publication_status, publication_link, abstract) VALUES
(1, 'Deep Transformer Architectures for Autonomous Decision Systems', 'Journal', 'IEEE Transactions on Pattern Analysis and Machine Intelligence', 'Artificial Intelligence', 2026, '10.1109/TPAMI.2026.1045678', '0162-8828', 'IEEE', 'Published', 'https://doi.org/10.1109/TPAMI.2026.1045678', 'This paper introduces an energy-efficient multi-head attention transformer model optimized for edge computing and real-time robotic navigation systems.'),
(1, 'Federated Learning Framework for Multi-Institutional Medical Imaging', 'Journal', 'Nature Machine Intelligence', 'Machine Learning', 2025, '10.1038/s42256-025-00481-x', '2522-5839', 'Springer Nature', 'Published', 'https://nature.com/articles/s42256-025-00481-x', 'We present a privacy-preserving federated learning paradigm that allows hospitals to collaboratively train diagnostic models without exchanging raw patient imaging records.'),
(2, 'Fault-Tolerant Microservices Scheduling in Hybrid Multi-Cloud Environments', 'Conference', 'ACM International Conference on Architectural Support for Programming Languages (ASPLOS)', 'Cloud Computing', 2026, '10.1145/3620666.3651340', '979-8-4007-0385-0', 'ACM', 'Published', 'https://doi.org/10.1145/3620666.3651340', 'An adaptive dynamic scheduler that mitigates tail latencies across heterogeneous multi-cloud container clusters during network partitioning.'),
(2, 'Zero-Downtime Live Migration of High-Throughput Stream Processors', 'Journal', 'IEEE Transactions on Cloud Computing', 'Distributed Systems', 2024, '10.1109/TCC.2024.3312345', '2168-7161', 'IEEE', 'Published', 'https://doi.org/10.1109/TCC.2024.3312345', 'A lightweight state-snapshotting mechanism for Apache Flink state backends supporting millisecond pause times during node migrations.'),
(3, 'Zero-Knowledge Proofs for Verifiable Credential Exchange in Decentralized Identity', 'Journal', 'ACM Transactions on Privacy and Security', 'Cyber Security', 2025, '10.1145/3589123.3590001', '2471-2566', 'ACM', 'Published', 'https://doi.org/10.1145/3589123.3590001', 'Explores zk-SNARK cryptographic proofs to achieve provable anonymity and regulatory compliance in decentralized academic identity systems.'),
(3, 'Quantum-Resistant Consensus Protocols for High-Throughput Enterprise Ledgers', 'Conference', 'IEEE Symposium on Security and Privacy (S&P)', 'Blockchain Technology', 2026, '10.1109/SP.2026.1098765', '978-1-6654-7890-1', 'IEEE', 'Accepted', 'https://sp2026.ieee-security.org', 'Proposes a post-quantum lattice-based signature scheme integrated into PBFT consensus, achieving 15,000 TPS under adversary simulation.'),
(4, 'Energy-Harvesting Ultra-Low-Power Sensor Nodes for Industrial IoT', 'Journal', 'Elsevier Internet of Things', 'Internet of Things (IoT)', 2024, '10.1016/j.iot.2024.100987', '2542-6605', 'Elsevier', 'Published', 'https://doi.org/10.1016/j.iot.2024.100987', 'Design and tape-out verification of a thermoelectric vibration harvester delivering autonomous power for pipeline pressure sensing.'),
(4, 'Deep Neural Accelerator on RISC-V Architecture for Smart Edge Sensors', 'Conference', 'IEEE International Solid-State Circuits Conference (ISSCC)', 'VLSI Design', 2025, '10.1109/ISSCC.2025.1011122', '978-1-5386-8888-0', 'IEEE', 'Published', 'https://doi.org/10.1109/ISSCC.2025.1011122', 'Demonstrates an 8-bit quantized integer matrix-multiplier integrated alongside a 64-bit RV64IMAFDC core fabricated in 16nm FinFET.'),
(5, 'Adaptive Reinforcement Learning Control for Soft Robotic Grippers', 'Journal', 'IEEE Robotics and Automation Letters', 'Robotics & Automation', 2025, '10.1109/LRA.2025.3214567', '2377-3766', 'IEEE', 'Published', 'https://doi.org/10.1109/LRA.2025.3214567', 'Presents a closed-loop tactile feedback policy enabling compliant soft grippers to handle delicate agricultural produce without damage.'),
(6, 'Multimodal Sentiment Analysis in Low-Resource Regional Languages using LLMs', 'Conference', 'Annual Meeting of the Association for Computational Linguistics (ACL)', 'NLP', 2026, '10.18653/v1/2026.acl-long.112', '978-1-959429-00-5', 'ACL', 'Under Review', 'https://aclanthology.org', 'Introduces cross-lingual adapter modules fine-tuned on code-mixed Hinglish and Dravidian social media video transcripts.');

-- =====================================================================
-- VERIFICATION QUERIES
-- =====================================================================
-- Check counts:
SELECT 'Total Users' AS Metric, COUNT(*) AS Value FROM users
UNION ALL
SELECT 'Total Faculty', COUNT(*) FROM faculty
UNION ALL
SELECT 'Total Publications', COUNT(*) FROM publications
UNION ALL
SELECT 'Publications (2026)', COUNT(*) FROM publications WHERE publication_year = 2026;
