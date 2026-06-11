# Product Requirements Document

## Purpose

The Major Availability Incident Management System is a portfolio project that demonstrates how a monolithic enterprise application can manage high-impact availability incidents.

## Users

- Incident Commander
- Communication Lead
- Technical Lead
- Operations Manager
- Support Manager

## Core Workflow

Detect -> Communicate -> Assess -> Delegate -> Resolve -> RCA -> Close

## Required Capabilities

- Dashboard showing total, open, resolved/RCA, closed, and status counts
- Incident Board organized by lifecycle state
- Incident Details page with roles, impact, timeline, and workflow actions
- Timeline events for all major operational updates
- RCA capture with owner, due date, corrective actions, preventive actions, and approval
- Knowledge Base for runbooks, process notes, and incident response standards

## Explicit Exclusions

Authentication, authorization, JWT, SSO, email integration, microservices, Kafka, and RabbitMQ are intentionally excluded.
