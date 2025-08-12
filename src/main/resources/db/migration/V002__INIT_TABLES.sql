INSERT INTO `tb_environment` (`name`) VALUES
    ('DEV'),
    ('QA'),
    ('UAT'),
    ('PROD');

INSERT INTO `tb_application` (`code`, `name`) VALUES
    ('CRE01', 'Core Banking System'),
    ('CRM02', 'Customer Relationship Manager'),
    ('LND03', 'Loan Origination Platform'),
    ('TRD04', 'Treasury & Trading System'),
    ('FXM05', 'Foreign Exchange Management'),
    ('RPT06', 'Regulatory Reporting Suite'),
    ('MOB07', 'Mobile Banking App'),
    ('IBK08', 'Internet Banking Portal'),
    ('KYC09', 'Know Your Customer Platform'),
    ('RISK1', 'Risk Management & Compliance Tool');

INSERT INTO `tb_credential_type` (`name`) VALUES
    ('DATABASE'),
    ('WINDOWS'),
    ('LINUX'),
    ('API_KEY'),
    ('JWT_TOKEN'),
    ('OTHER');