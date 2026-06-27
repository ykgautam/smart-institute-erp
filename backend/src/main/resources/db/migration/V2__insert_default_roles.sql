INSERT INTO roles
(
    name,
    description,
    created_at,
    updated_at,
    created_by,
    updated_by
)
VALUES
    ('SUPER_ADMIN','Complete access to the entire ERP platform',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'SYSTEM','SYSTEM'),
    ('INSTITUTE_ADMIN','Manages institute operations and configuration',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'SYSTEM','SYSTEM'),
    ('FACULTY','Manages classes, attendance and assessments',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'SYSTEM','SYSTEM'),
    ('STAFF','Performs administrative office tasks',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'SYSTEM','SYSTEM'),
    ('ACCOUNTANT','Handles fees, expenses and financial records',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'SYSTEM','SYSTEM'),
    ('STUDENT','Accesses personal academic information',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'SYSTEM','SYSTEM');