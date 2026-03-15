-- ==========================================
-- 1. RESET DATABASE 
-- ==========================================
USE master;
GO
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'ParkingManagement')
BEGIN
    ALTER DATABASE ParkingManagement SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ParkingManagement;
END
GO
CREATE DATABASE ParkingManagement;
GO
USE ParkingManagement;
GO

-- ==========================================
-- 2. TẠO CẤU TRÚC BẢNG
-- ==========================================
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY IDENTITY(1,1),
    RoleName NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    Username VARCHAR(50) NOT NULL UNIQUE,
    PasswordHash VARCHAR(50) NOT NULL,
    FullName NVARCHAR(100),
    PhoneNumber VARCHAR(15),
    Email VARCHAR(100) NOT NULL UNIQUE,
    IsEmailVerified BIT DEFAULT 0,
    DateOfBirth DATE,
    Avatar VARCHAR(255),
    RoleID INT FOREIGN KEY REFERENCES Roles(RoleID),
    IsActive BIT DEFAULT 1
);

CREATE TABLE VehicleTypes (
    TypeID INT PRIMARY KEY IDENTITY(1,1),
    TypeName NVARCHAR(50) NOT NULL,
    PricePerHour DECIMAL(18, 2) DEFAULT 0,
    PricePerMonth DECIMAL(18, 2) DEFAULT 0
);

CREATE TABLE ParkingSlots (
    SlotID INT PRIMARY KEY IDENTITY(1,1),
    SlotCode VARCHAR(10) NOT NULL UNIQUE, 
    Zone NVARCHAR(50), 
    TypeID INT FOREIGN KEY REFERENCES VehicleTypes(TypeID), 
    Status NVARCHAR(20) DEFAULT 'Available' -- Available, Occupied, Maintenance, Reserved
);

CREATE TABLE MonthlyPasses (
    PassID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT FOREIGN KEY REFERENCES Users(UserID), 
    SlotID INT FOREIGN KEY REFERENCES ParkingSlots(SlotID), 
    LicensePlate VARCHAR(20) NOT NULL UNIQUE, 
    TypeID INT FOREIGN KEY REFERENCES VehicleTypes(TypeID),
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    IsActive BIT DEFAULT 1
);

CREATE TABLE Tickets (
    TicketID INT PRIMARY KEY IDENTITY(1,1),
    LicensePlate VARCHAR(20) NOT NULL,
    SlotID INT FOREIGN KEY REFERENCES ParkingSlots(SlotID),
    TypeID INT FOREIGN KEY REFERENCES VehicleTypes(TypeID),
    IsMonthlyPass BIT DEFAULT 0, 
    CheckInTime DATETIME DEFAULT GETDATE(),
    CheckOutTime DATETIME NULL,
    TotalFee DECIMAL(18, 2) DEFAULT 0,
    StaffInID INT FOREIGN KEY REFERENCES Users(UserID),
    StaffOutID INT FOREIGN KEY REFERENCES Users(UserID),
    Status NVARCHAR(20) DEFAULT 'Active'
);
GO

-- ==========================================
-- 3. DỮ LIỆU MẪU
-- ==========================================

-- 3.1. Vai trò
INSERT INTO Roles (RoleName) VALUES ('Admin'), ('Staff'), ('Customer');

-- 3.2. Người dùng (1 Admin, 2 Staff, 5 Customer)
INSERT INTO Users (Username, PasswordHash, FullName, PhoneNumber, Email, DateOfBirth, RoleID) VALUES 
('admin', 'admin123', N'Quản Trị Viên', '0901234567', 'admin@iparking.com', '1990-01-01', 1),
('staff01', 'staff123', N'Trần Thị Thu Ngân', '0912345678', 'staff01@iparking.com', '1995-05-12', 2),
('staff02', 'staff123', N'Lê Văn Trực Ca', '0922334455', 'staff02@iparking.com', '1998-08-20', 2),
('khach01', 'khach123', N'Nguyễn Văn Một', '0988111001', 'khach01@gmail.com', '1985-03-15', 3), 
('khach02', 'khach123', N'Lê Thị Hai', '0988111002', 'khach02@gmail.com', '1992-07-22', 3),   
('khach03', 'khach123', N'Trần Văn Ba', '0988111003', 'khach03@gmail.com', '1988-11-05', 3),
('khach04', 'khach123', N'Phạm Thị Bốn', '0988111004', 'khach04@gmail.com', '1996-12-10', 3),
('khach05', 'khach123', N'Hoàng Văn Năm (Hết Hạn)', '0988111005', 'khach05@gmail.com', '1980-02-28', 3);

-- 3.3. Bảng giá (3 loại xe)
INSERT INTO VehicleTypes (TypeName, PricePerHour, PricePerMonth) VALUES 
(N'Xe máy', 5000, 150000),             -- Type 1
(N'Ô tô 4-5 chỗ', 20000, 1500000),     -- Type 2
(N'Xe đạp / Xe điện', 2000, 50000);    -- Type 3

-- 3.4. Vị trí đỗ xe (17 chỗ đỗ)
-- KHU A (Xe máy - Type 1)
INSERT INTO ParkingSlots (SlotCode, Zone, TypeID, Status) VALUES 
('A-01', N'Khu Xe Máy', 1, 'Occupied'),  -- Xe vãng lai đang đỗ
('A-02', N'Khu Xe Máy', 1, 'Reserved'),  -- Chỗ của khach01 (Hiện đang đi vắng)
('A-03', N'Khu Xe Máy', 1, 'Available'), 
('A-04', N'Khu Xe Máy', 1, 'Occupied'),  -- Xe khach04 đang đỗ
('A-05', N'Khu Xe Máy', 1, 'Available'), 
('A-06', N'Khu Xe Máy', 1, 'Maintenance'), -- Đang hỏng/bảo trì
('A-07', N'Khu Xe Máy', 1, 'Reserved'),  -- Chỗ của khach05 (Vé hết hạn)
('A-08', N'Khu Xe Máy', 1, 'Available'); 

-- KHU B (Ô tô - Type 2)
INSERT INTO ParkingSlots (SlotCode, Zone, TypeID, Status) VALUES 
('B-01', N'Khu Ô tô', 2, 'Occupied'),    -- Xe khach02 đang đỗ
('B-02', N'Khu Ô tô', 2, 'Available'),
('B-03', N'Khu Ô tô', 2, 'Reserved'),    -- Chỗ của khach03 (Hiện đang đi vắng)
('B-04', N'Khu Ô tô', 2, 'Available'),
('B-05', N'Khu Ô tô', 2, 'Occupied'),    -- Xe vãng lai đang đỗ
('B-06', N'Khu Ô tô', 2, 'Available');

-- KHU C (Xe đạp/điện - Type 3)
INSERT INTO ParkingSlots (SlotCode, Zone, TypeID, Status) VALUES 
('C-01', N'Khu Xe Đạp', 3, 'Available'),
('C-02', N'Khu Xe Đạp', 3, 'Occupied'),  -- Xe vãng lai đang đỗ
('C-03', N'Khu Xe Đạp', 3, 'Available');

-- 3.5. Cấp vé tháng cho khách hàng 
INSERT INTO MonthlyPasses (UserID, SlotID, LicensePlate, TypeID, StartDate, EndDate) VALUES 
(4, 2, '29M1-111.11', 1, '2026-01-01', '2026-12-31'), -- khach01 (Chỗ A-02) - Còn hạn dài
(5, 9, '30A-222.22', 2, '2026-03-01', '2026-04-01'),  -- khach02 (Chỗ B-01) - Còn hạn
(6, 11, '30A-333.33', 2, '2026-02-15', '2026-08-15'), -- khach03 (Chỗ B-03) - Còn hạn
(7, 4, '29M1-444.44', 1, '2026-03-01', '2026-06-01'), -- khach04 (Chỗ A-04) - Còn hạn
(8, 7, '29M1-555.55', 1, '2026-01-01', '2026-02-01'); -- khach05 (Chỗ A-07) - ĐÃ HẾT HẠN (Để test logic từ chối vào bãi)

-- 3.6. Lịch sử giao dịch (Tickets)
-- [A] CÁC XE ĐANG GỬI TRONG BÃI (Active) -> Khớp với 5 chỗ Occupied ở trên
-- Vãng lai xe máy đỗ A-01
INSERT INTO Tickets (LicensePlate, SlotID, TypeID, IsMonthlyPass, CheckInTime, TotalFee, StaffInID, Status) VALUES 
('29Z1-999.88', 1, 1, 0, DATEADD(hour, -2, GETDATE()), 0, 2, 'Active');  

-- Khách vé tháng 04 đỗ xe máy ở A-04 (Phí 0đ)
INSERT INTO Tickets (LicensePlate, SlotID, TypeID, IsMonthlyPass, CheckInTime, TotalFee, StaffInID, Status) VALUES 
('29M1-444.44', 4, 1, 1, DATEADD(hour, -5, GETDATE()), 0, 2, 'Active'); 

-- Khách vé tháng 02 đỗ ô tô ở B-01 (Phí 0đ)
INSERT INTO Tickets (LicensePlate, SlotID, TypeID, IsMonthlyPass, CheckInTime, TotalFee, StaffInID, Status) VALUES 
('30A-222.22', 9, 2, 1, DATEADD(hour, -10, GETDATE()), 0, 3, 'Active'); 

-- Vãng lai ô tô đỗ B-05
INSERT INTO Tickets (LicensePlate, SlotID, TypeID, IsMonthlyPass, CheckInTime, TotalFee, StaffInID, Status) VALUES 
('51F-777.66', 13, 2, 0, DATEADD(minute, -45, GETDATE()), 0, 3, 'Active'); 

-- Vãng lai xe đạp đỗ C-02
INSERT INTO Tickets (LicensePlate, SlotID, TypeID, IsMonthlyPass, CheckInTime, TotalFee, StaffInID, Status) VALUES 
('XEDAP-001', 16, 3, 0, DATEADD(minute, -120, GETDATE()), 0, 2, 'Active'); 

-- [B] CÁC XE VÃNG LAI ĐÃ RỜI ĐI (Completed) -> 10 giao dịch để test Báo cáo doanh thu
INSERT INTO Tickets (LicensePlate, SlotID, TypeID, IsMonthlyPass, CheckInTime, CheckOutTime, TotalFee, StaffInID, StaffOutID, Status) VALUES 
('29X1-000.01', 3, 1, 0, DATEADD(hour, -20, GETDATE()), DATEADD(hour, -18, GETDATE()), 10000, 2, 2, 'Completed'), -- Xe máy 2h: 10k
('29X1-000.02', 5, 1, 0, DATEADD(hour, -15, GETDATE()), DATEADD(hour, -14, GETDATE()), 5000, 2, 3, 'Completed'),  -- Xe máy 1h: 5k
('30A-000.03', 10, 2, 0, DATEADD(hour, -48, GETDATE()), DATEADD(hour, -40, GETDATE()), 160000, 3, 2, 'Completed'), -- Ô tô 8h hôm kia: 160k
('30A-000.04', 12, 2, 0, DATEADD(hour, -24, GETDATE()), DATEADD(hour, -22, GETDATE()), 40000, 3, 3, 'Completed'),  -- Ô tô 2h hôm qua: 40k
('29X1-000.05', 8, 1, 0, DATEADD(hour, -10, GETDATE()), DATEADD(hour, -5, GETDATE()), 25000, 2, 2, 'Completed'),  -- Xe máy 5h: 25k
('XEDAP-002', 15, 3, 0, DATEADD(hour, -6, GETDATE()), DATEADD(hour, -4, GETDATE()), 4000, 2, 3, 'Completed'),     -- Xe đạp 2h: 4k
('30A-000.06', 14, 2, 0, DATEADD(hour, -8, GETDATE()), DATEADD(hour, -7, GETDATE()), 20000, 3, 2, 'Completed'),   -- Ô tô 1h: 20k
('29X1-000.07', 3, 1, 0, DATEADD(hour, -5, GETDATE()), DATEADD(hour, -4, GETDATE()), 5000, 2, 2, 'Completed'),    -- Xe máy 1h: 5k
('29X1-000.08', 5, 1, 0, DATEADD(hour, -4, GETDATE()), DATEADD(hour, -1, GETDATE()), 15000, 3, 3, 'Completed'),   -- Xe máy 3h: 15k
('XEDAP-003', 17, 3, 0, DATEADD(hour, -3, GETDATE()), DATEADD(hour, -2, GETDATE()), 2000, 2, 2, 'Completed');     -- Xe đạp 1h: 2k
GO