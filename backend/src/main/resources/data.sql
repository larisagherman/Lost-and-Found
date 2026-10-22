-- Institutions
INSERT INTO institution (name, code, is_active)
VALUES
    ('The Polytechnic University of Timisoara', 'UPT', TRUE),
    ('West University of Timisoara', 'UVT', TRUE)
ON CONFLICT (code) DO NOTHING;

-- Faculties are linked by institution code so the seed does not depend on generated IDs.
INSERT INTO faculty (name, code, institution_id)
SELECT seed.name, seed.code, institution.id
FROM (VALUES
    ('Faculty of Architecture and Urban Planning', 'UPT-AC', 'UPT'),
    ('Faculty of Automation and Computers', 'UPT-ACCT', 'UPT'),
    ('Faculty of Civil Engineering', 'UPT-C', 'UPT'),
    ('Faculty of Communication Sciences', 'UPT-COM', 'UPT'),
    ('Faculty of Electrical and Power Engineering', 'UPT-ET', 'UPT'),
    ('Faculty of Electronics, Telecommunications and Information Technologies', 'UPT-ETC', 'UPT'),
    ('Faculty of Hydrotechnical Engineering', 'UPT-H', 'UPT'),
    ('Faculty of Industrial Chemistry and Environmental Engineering', 'UPT-IC', 'UPT'),
    ('Faculty of Management in Production and Transportation', 'UPT-MPT', 'UPT'),
    ('Faculty of Mechanical Engineering', 'UPT-MEC', 'UPT'),
    ('Faculty of Transportation', 'UPT-TR', 'UPT'),
    ('Faculty of Economics and Business Administration', 'UVT-FEAA', 'UVT'),
    ('Faculty of Law', 'UVT-FL', 'UVT'),
    ('Faculty of Letters, History and Theology', 'UVT-FLHT', 'UVT'),
    ('Faculty of Mathematics and Computer Science', 'UVT-FMI', 'UVT'),
    ('Faculty of Physics', 'UVT-FIZ', 'UVT'),
    ('Faculty of Political Sciences, Philosophy and Communication Sciences', 'UVT-FSPC', 'UVT'),
    ('Faculty of Sociology and Psychology', 'UVT-FSP', 'UVT'),
    ('Faculty of Chemistry, Biology and Geography', 'UVT-CBG', 'UVT')
) AS seed(name, code, institution_code)
JOIN institution ON institution.code = seed.institution_code
ON CONFLICT (code) DO NOTHING;

-- Sample users. Passwords are intentionally not included because the current
-- Users entity has no password/authentication field.
INSERT INTO users (display_name, email, role, institution_id, faculty_id)
SELECT seed.display_name,
       seed.email,
       seed.role,
       institution.id,
       faculty.id
FROM (VALUES
    ('Ana Popescu', 'ana.popescu@student.upt.ro', 'STUDENT', 'UPT', 'UPT-ACCT'),
    ('Mihai Ionescu', 'mihai.ionescu@student.upt.ro', 'STUDENT', 'UPT', 'UPT-ETC'),
    ('Elena Stan', 'elena.stan@student.uvt.ro', 'STUDENT', 'UVT', 'UVT-FMI'),
    ('Vlad Dumitrescu', 'vlad.dumitrescu@student.uvt.ro', 'STUDENT', 'UVT', 'UVT-FEAA'),
    ('UPT Lost and Found Admin', 'lostfound.admin@upt.ro', 'ADMIN', 'UPT', NULL),
    ('UVT Lost and Found Admin', 'lostfound.admin@uvt.ro', 'ADMIN', 'UVT', NULL)
) AS seed(display_name, email, role, institution_code, faculty_code)
JOIN institution ON institution.code = seed.institution_code
LEFT JOIN faculty ON faculty.code = seed.faculty_code
    AND faculty.institution_id = institution.id
ON CONFLICT (email) DO NOTHING;

-- Reports belong to a user. Institution and faculty are available through
-- the reporter relationship, avoiding duplicate foreign keys on the report.
INSERT INTO item_report (
    reporter_id,
    item_name,
    item_details,
    category,
    colour,
    brand,
    location,
    incident_at,
    item_report_type,
    item_report_status,
    created_at
)
SELECT reporter.id,
       seed.item_name,
       seed.item_details,
       seed.category,
       seed.colour,
       seed.brand,
       seed.location,
       seed.incident_at,
       seed.item_report_type,
       seed.item_report_status,
       seed.created_at
FROM (VALUES
    ('Black laptop', '15-inch laptop with a small sticker on the lid', 'ELECTRONICS', 'Black', 'Lenovo', 'UPT Library, first floor', TIMESTAMP '2026-10-14 10:30:00', 'LOST', 'OPEN', TIMESTAMP '2026-10-14 12:00:00', 'ana.popescu@student.upt.ro'),
    ('Blue student backpack', 'Contains two notebooks and a water bottle', 'BAGS', 'Blue', 'Eastpak', 'UVT Central Building, room 214', TIMESTAMP '2026-10-15 15:00:00', 'LOST', 'OPEN', TIMESTAMP '2026-10-15 16:10:00', 'elena.stan@student.uvt.ro'),
    ('Silver keys', 'Three keys on a red fabric keyring', 'PERSONAL belongings', 'Silver', NULL, 'UPT Faculty of Automation and Computers', TIMESTAMP '2026-10-16 08:45:00', 'FOUND', 'OPEN', TIMESTAMP '2026-10-16 09:15:00', 'mihai.ionescu@student.upt.ro'),
    ('Wireless earbuds', 'White charging case with a scratch on the front', 'ELECTRONICS', 'White', 'Samsung', 'UVT cafeteria', TIMESTAMP '2026-10-16 13:20:00', 'FOUND', 'OPEN', TIMESTAMP '2026-10-16 14:00:00', 'vlad.dumitrescu@student.uvt.ro'),
    ('Student ID card', 'Name partly visible; card belongs to a UPT student', 'DOCUMENTS', NULL, NULL, 'UPT main entrance', TIMESTAMP '2026-10-17 09:00:00', 'FOUND', 'OPEN', TIMESTAMP '2026-10-17 09:20:00', 'lostfound.admin@upt.ro'),
    ('Green hoodie', 'Size M, white logo on the chest', 'CLOTHING', 'Green', 'Nike', 'UVT Sports Hall', TIMESTAMP '2026-10-17 18:30:00', 'LOST', 'OPEN', TIMESTAMP '2026-10-17 19:00:00', 'vlad.dumitrescu@student.uvt.ro'),
    ('Red water bottle', 'Metal bottle with a dent near the bottom', 'PERSONAL belongings', 'Red', 'Hydro Flask', 'UVT Library reading room', TIMESTAMP '2026-10-18 11:15:00', 'FOUND', 'OPEN', TIMESTAMP '2026-10-18 11:45:00', 'elena.stan@student.uvt.ro'),
    ('Calculator', 'Scientific calculator with initials written on the back', 'STUDY MATERIALS', 'Black', 'Casio', 'UPT Faculty of Civil Engineering, room 105', TIMESTAMP '2026-10-18 14:40:00', 'LOST', 'OPEN', TIMESTAMP '2026-10-18 15:10:00', 'ana.popescu@student.upt.ro'),
    ('Keyring with three keys', 'Three silver keys on a red fabric keyring', 'PERSONAL belongings', 'Silver', NULL, 'UPT Faculty of Automation and Computers', TIMESTAMP '2026-10-15 08:30:00', 'LOST', 'OPEN', TIMESTAMP '2026-10-15 10:00:00', 'ana.popescu@student.upt.ro'),
    ('Wireless earbuds case', 'White Samsung earbuds case with a scratch on the front', 'ELECTRONICS', 'White', 'Samsung', 'UVT cafeteria', TIMESTAMP '2026-10-16 12:50:00', 'LOST', 'OPEN', TIMESTAMP '2026-10-16 14:10:00', 'vlad.dumitrescu@student.uvt.ro'),
    ('Metal water bottle', 'Red Hydro Flask bottle with a dent near the bottom', 'PERSONAL belongings', 'Red', 'Hydro Flask', 'UVT Library reading room', TIMESTAMP '2026-10-18 10:45:00', 'LOST', 'OPEN', TIMESTAMP '2026-10-18 11:50:00', 'elena.stan@student.uvt.ro')
) AS seed(
    item_name,
    item_details,
    category,
    colour,
    brand,
    location,
    incident_at,
    item_report_type,
    item_report_status,
    created_at,
    reporter_email
)
JOIN users reporter ON reporter.email = seed.reporter_email
WHERE NOT EXISTS (
    SELECT 1
    FROM item_report existing
    WHERE existing.item_name = seed.item_name
      AND existing.location = seed.location
      AND existing.incident_at = seed.incident_at
      AND existing.item_report_type = seed.item_report_type
);

-- Suggested matches for three of the found reports. The ID card intentionally
-- remains unmatched because there is no corresponding lost report in the seed.
INSERT INTO match_item (
    lost_item_report_id,
    found_item_report_id,
    match_status,
    created_at
)
SELECT lost.id,
       found.id,
       'SUGGESTED',
       TIMESTAMP '2026-10-20 09:00:00'
FROM (
    VALUES
        ('Keyring with three keys', 'Silver keys'),
        ('Wireless earbuds case', 'Wireless earbuds'),
        ('Metal water bottle', 'Red water bottle')
) AS pairs(lost_item_name, found_item_name)
JOIN item_report lost
    ON lost.item_name = pairs.lost_item_name
   AND lost.item_report_type = 'LOST'
JOIN item_report found
    ON found.item_name = pairs.found_item_name
   AND found.item_report_type = 'FOUND'
WHERE NOT EXISTS (
    SELECT 1
    FROM match_item existing
    WHERE existing.lost_item_report_id = lost.id
      AND existing.found_item_report_id = found.id
);
