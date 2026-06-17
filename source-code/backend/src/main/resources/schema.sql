DROP TABLE IF EXISTS incident_timeline;
DROP TABLE IF EXISTS incident_rca;
DROP TABLE IF EXISTS incident_resolution;
DROP TABLE IF EXISTS incident_assessment;
DROP TABLE IF EXISTS knowledge_base;
DROP TABLE IF EXISTS app_users;
DROP TABLE IF EXISTS incidents;

CREATE TABLE app_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    display_name VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE incidents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(160) NOT NULL,
    description TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(32) NOT NULL,
    impacted_service VARCHAR(120) NOT NULL,
    incident_commander VARCHAR(120) NOT NULL,
    communication_lead VARCHAR(120) NOT NULL,
    technical_lead VARCHAR(120) NOT NULL,
    reporter VARCHAR(120) NOT NULL,
    team_lead VARCHAR(120) NOT NULL,
    escalation_manager VARCHAR(120) NOT NULL,
    senior_manager VARCHAR(120) NOT NULL,
    current_owner VARCHAR(120) NOT NULL,
    current_owner_role VARCHAR(80) NOT NULL,
    detected_at DATETIME NOT NULL,
    eta_due_at DATETIME NOT NULL,
    resolved_at DATETIME NULL,
    closed_at DATETIME NULL
);

CREATE TABLE incident_assessment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    incident_id BIGINT NOT NULL UNIQUE,
    customer_impact TEXT NOT NULL,
    business_impact TEXT NOT NULL,
    affected_users INT NOT NULL,
    current_hypothesis TEXT NOT NULL,
    assessed_at DATETIME NOT NULL,
    CONSTRAINT fk_assessment_incident FOREIGN KEY (incident_id) REFERENCES incidents(id)
);

CREATE TABLE incident_resolution (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    incident_id BIGINT NOT NULL UNIQUE,
    resolution_summary TEXT NOT NULL,
    mitigation_steps TEXT NOT NULL,
    resolved_by VARCHAR(120) NOT NULL,
    resolved_at DATETIME NOT NULL,
    CONSTRAINT fk_resolution_incident FOREIGN KEY (incident_id) REFERENCES incidents(id)
);

CREATE TABLE incident_rca (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    incident_id BIGINT NOT NULL UNIQUE,
    root_cause TEXT NOT NULL,
    contributing_factors TEXT NOT NULL,
    corrective_actions TEXT NOT NULL,
    preventive_actions TEXT NOT NULL,
    owner VARCHAR(120) NOT NULL,
    due_date DATE NOT NULL,
    approved BOOLEAN NOT NULL,
    CONSTRAINT fk_rca_incident FOREIGN KEY (incident_id) REFERENCES incidents(id)
);

CREATE TABLE incident_timeline (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    incident_id BIGINT NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    actor VARCHAR(140) NOT NULL,
    message TEXT NOT NULL,
    occurred_at DATETIME NOT NULL,
    CONSTRAINT fk_timeline_incident FOREIGN KEY (incident_id) REFERENCES incidents(id)
);

CREATE TABLE knowledge_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(160) NOT NULL,
    category VARCHAR(80) NOT NULL,
    summary TEXT NOT NULL,
    content TEXT NOT NULL,
    tags VARCHAR(200) NOT NULL,
    updated_at DATETIME NOT NULL
);
