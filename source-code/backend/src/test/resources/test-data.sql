INSERT INTO incidents (id, title, description, severity, status, impacted_service, incident_commander, communication_lead, technical_lead, reporter, team_lead, escalation_manager, senior_manager, current_owner, current_owner_role, detected_at, eta_due_at, resolved_at, closed_at) VALUES
(1, 'Checkout API elevated error rate', 'Customers are intermittently unable to complete card payments during peak traffic.', 'SEV1', 'DELEGATED', 'Checkout API', 'Aarav Mehta', 'Neha Rao', 'Vikram Shah', 'Synthetic Monitor', 'Aarav Mehta', 'Meera Joshi', 'Rehan Kapur', 'Meera Joshi', 'Escalation Manager', '2026-06-04 09:10:00', '2026-06-04 09:40:00', NULL, NULL),
(2, 'Mobile app login latency', 'Login calls from the Android app are taking more than 15 seconds for a subset of users.', 'SEV2', 'RESOLVED', 'Identity Gateway', 'Maya Singh', 'Rohan Iyer', 'Sara Dsouza', 'APM Alert', 'Maya Singh', 'Anika Rao', 'Nikhil Batra', 'Nikhil Batra', 'Senior Manager', '2026-06-03 14:25:00', '2026-06-03 16:00:00', '2026-06-03 16:05:00', NULL),
(3, 'Reporting warehouse refresh failed', 'Daily executive dashboards are showing stale revenue and availability figures.', 'SEV3', 'CLOSED', 'Analytics Warehouse', 'Kabir Khan', 'Isha Nair', 'Dev Patel', 'Data Quality Monitor', 'Kabir Khan', 'Pooja Shah', 'Sameer Desai', 'Sameer Desai', 'Senior Manager', '2026-06-01 06:30:00', '2026-06-01 08:00:00', '2026-06-01 08:10:00', '2026-06-02 17:30:00'),
(4, 'Public status page update delay', 'Status page component updates are delayed after incident communications are posted.', 'SEV2', 'COMMUNICATING', 'Status Page', 'Lina Fernandes', 'Om Prakash', 'Tara Bose', 'Operations Desk', 'Lina Fernandes', 'Karan Malhotra', 'Diya Sethi', 'Lina Fernandes', 'Team Lead', '2026-06-04 10:40:00', '2026-06-04 11:10:00', NULL, NULL);

INSERT INTO incident_assessment (incident_id, customer_impact, business_impact, affected_users, current_hypothesis, assessed_at) VALUES
(1, 'Payment attempts fail or time out for approximately 18% of checkout sessions.', 'Revenue capture is reduced and support contacts are increasing.', 4200, 'A connection pool regression in the payment adapter is exhausting worker threads.', '2026-06-04 09:35:00'),
(2, 'Users experience slow login and occasional retries before reaching the home screen.', 'Conversion from push campaigns dropped during the window.', 9500, 'Cache misses caused the identity gateway to fall back to a slower profile lookup path.', '2026-06-03 14:45:00'),
(3, 'Internal leadership dashboards show stale data only; no customer-facing impact.', 'Morning operating review used manual extracts.', 85, 'Warehouse job failed after a schema drift in the billing export.', '2026-06-01 07:00:00');

INSERT INTO incident_resolution (incident_id, resolution_summary, mitigation_steps, resolved_by, resolved_at) VALUES
(2, 'Login latency returned to baseline after restoring gateway cache warming and increasing profile lookup timeout budgets.', 'Replayed cache warmup, scaled identity gateway pods, and validated Android login traces.', 'Sara Dsouza', '2026-06-03 16:05:00'),
(3, 'Warehouse refresh completed after the billing export mapping was corrected.', 'Patched the loader mapping, reran failed partition, and validated dashboard freshness.', 'Dev Patel', '2026-06-01 08:10:00');

INSERT INTO incident_rca (incident_id, root_cause, contributing_factors, corrective_actions, preventive_actions, owner, due_date, approved) VALUES
(3, 'Billing export added a nullable promotion_code column without updating the warehouse ingestion contract.', 'Contract tests covered required fields but not additive nullable columns. Alert routing also targeted the analytics channel only.', 'Add ingestion contract tests for additive fields and route failed refreshes to the incident operations channel.', 'Publish schema-change checklist and add pre-production warehouse replay for billing exports.', 'Kabir Khan', '2026-06-10', TRUE);

INSERT INTO incident_timeline (incident_id, event_type, actor, message, occurred_at) VALUES
(1, 'DETECTED', 'Synthetic Monitor', 'Checkout error rate exceeded SEV1 threshold.', '2026-06-04 09:10:00'),
(1, 'COMMUNICATION', 'Neha Rao', 'Initial customer impact update posted to the public status page.', '2026-06-04 09:18:00'),
(1, 'ASSESSMENT', 'Aarav Mehta', 'Incident assessed as payment adapter thread exhaustion.', '2026-06-04 09:35:00'),
(1, 'DELEGATION', 'Aarav Mehta', 'Payments team owns mitigation; communications lead will update every 20 minutes.', '2026-06-04 09:42:00'),
(2, 'DETECTED', 'APM Alert', 'P95 login latency exceeded 15 seconds.', '2026-06-03 14:25:00'),
(2, 'COMMUNICATION', 'Rohan Iyer', 'Internal support advisory published for slow mobile logins.', '2026-06-03 14:32:00'),
(2, 'ASSESSMENT', 'Maya Singh', 'Identity cache misses confirmed as primary driver.', '2026-06-03 14:45:00'),
(2, 'DELEGATION', 'Maya Singh', 'Identity gateway team assigned cache recovery actions.', '2026-06-03 14:52:00'),
(2, 'RESOLUTION', 'Sara Dsouza', 'Gateway cache restored and login latency normalized.', '2026-06-03 16:05:00'),
(3, 'DETECTED', 'Data Quality Monitor', 'Warehouse freshness check failed.', '2026-06-01 06:30:00'),
(3, 'ASSESSMENT', 'Kabir Khan', 'Impact limited to internal reporting.', '2026-06-01 07:00:00'),
(3, 'RESOLUTION', 'Dev Patel', 'Billing export mapping patched and refresh completed.', '2026-06-01 08:10:00'),
(3, 'RCA', 'Kabir Khan', 'RCA approved with contract test actions.', '2026-06-02 15:20:00'),
(3, 'CLOSURE', 'Kabir Khan', 'Incident closed after RCA approval and KB article publication.', '2026-06-02 17:30:00'),
(4, 'DETECTED', 'Operations Desk', 'Public status page queue depth exceeded warning threshold.', '2026-06-04 10:40:00'),
(4, 'COMMUNICATION', 'Om Prakash', 'Manual customer update posted while status page delay is investigated.', '2026-06-04 10:48:00');

INSERT INTO knowledge_base (title, category, summary, content, tags, updated_at) VALUES
('SEV1 checkout triage checklist', 'Runbook', 'Steps for confirming checkout impact, payment adapter health, and customer communication cadence.', 'Check payment adapter pool metrics, payment provider latency, checkout error budget burn, and synthetic checkout traces. Assign incident commander, communications lead, and technical lead before mitigation work begins.', 'checkout,payments,sev1,runbook', '2026-06-04 08:00:00'),
('Identity gateway cache recovery', 'Runbook', 'Procedure for restoring cache warmup and validating login latency recovery.', 'Validate cache hit ratio, replay warmup jobs, scale identity gateway pods if saturation is visible, and confirm mobile login traces across Android and iOS.', 'identity,login,cache,latency', '2026-06-03 17:00:00'),
('RCA writing standard', 'Process', 'Template guidance for root cause, contributing factors, corrective actions, preventive actions, and approval.', 'An RCA should be factual, blameless, time-bound, and connected to preventive controls. Every corrective action needs an owner and due date.', 'rca,process,closure', '2026-06-02 12:00:00');
