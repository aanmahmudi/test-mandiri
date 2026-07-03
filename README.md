# Mini Project Library Microservices

Mini project ini menggunakan `Spring Boot 3`, `Java 21`, `Lombok`, `PostgreSQL`, dan pola arsitektur berlapis:

- `entity`
- `repository`
- `dto`
- `service`
- `controller`

Project dibagi menjadi dua microservice:

- `catalog-service`: mengelola data buku dan stok buku
- `lending-service`: mengelola member dan transaksi peminjaman

## Struktur Project

```text
test-mandiri/
|-- catalog-service/
|-- lending-service/
|-- initdb/
|   `-- 01_init.sql
`-- docker-compose.yml
```

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Lombok
- Docker Compose

## Menjalankan Project

### Opsi 1: Jalankan per service dari IDE / terminal

Pastikan PostgreSQL sudah aktif dan database/schema tersedia.

Default konfigurasi saat ini:

- `catalog-service` berjalan di `http://localhost:8081`
- `lending-service` berjalan di `http://localhost:8082`

Masuk ke masing-masing service lalu jalankan:

```bash
cd d:\test-mandiri\catalog-service
mvn spring-boot:run
```

```bash
cd d:\test-mandiri\lending-service
mvn spring-boot:run
```

### Opsi 2: Jalankan dengan Docker Compose

Dari root project:

```bash
cd d:\test-mandiri
docker compose up --build
```

Service yang akan aktif:

- PostgreSQL pada port `5432`
- `catalog-service` pada port `8081`
- `lending-service` pada port `8082`

## Konfigurasi Database

Schema database diinisialisasi lewat:

- `catalog`
- `lending`

File inisialisasi schema:

- [01_init.sql](file:///d:/test-mandiri/initdb/01_init.sql)

## Endpoint Utama

### Catalog Service

Base URL: `http://localhost:8081`

- `POST /api/books`
- `GET /api/books/{id}`
- `GET /api/books?q=clean&limit=20`
- `PATCH /api/books/{id}/reserve`
- `PATCH /api/books/{id}/release`

### Lending Service

Base URL: `http://localhost:8082`

- `POST /api/members`
- `GET /api/members/{id}`
- `POST /api/loans`
- `POST /api/loans/{id}/return`
- `GET /api/loans/{id}?include_book=true`
- `GET /api/loans?member_id={member_id}`
- `GET /api/analytics/members/loan-summary?min_active=1`

## Contoh Penggunaan

Semua request JSON gunakan header:

```text
Content-Type: application/json
```

### 1. Tambah Buku

`POST http://localhost:8081/api/books`

```json
{
  "isbn": "978-602-0000-001",
  "title": "Clean Architecture",
  "author": "Robert C. Martin",
  "published_year": 2017,
  "stock_total": 3
}
```

### 2. Tambah Member

`POST http://localhost:8082/api/members`

```json
{
  "name": "Budi",
  "email": "budi@mail.com"
}
```

### 3. Buat Loan

`POST http://localhost:8082/api/loans`

```json
{
  "member_id": "isi-dengan-member-id",
  "book_id": "isi-dengan-book-id",
  "days": 14
}
```

### 4. Return Loan

`POST http://localhost:8082/api/loans/{loan_id}/return`

### 5. Cek Analytics

`GET http://localhost:8082/api/analytics/members/loan-summary?min_active=1`

## Urutan Test di Postman

Urutan test yang disarankan:

1. `Create Book`
2. `Get Book`
3. `Create Member`
4. `Get Member`
5. `Create Loan`
6. `Get Loan`
7. `List Loan By Member`
8. `Get Book` lagi untuk cek `stock_available`
9. `Return Loan`
10. `Get Book` lagi untuk pastikan stok kembali
11. `Analytics`

## Titik Singgung Catalog dan Lending

Hubungan antardua service bersifat satu arah:

- `lending-service` memanggil `catalog-service`
- `catalog-service` tidak memanggil `lending-service`

### Yang Bersinggungan

1. Ambil detail buku

- Saat `lending-service` butuh detail buku, dia memanggil endpoint `GET /api/books/{id}` di `catalog-service`

2. Reserve stok buku

- Saat loan dibuat, `lending-service` memanggil endpoint `PATCH /api/books/{id}/reserve`
- Tujuannya untuk mengurangi `stock_available`

3. Release stok buku

- Saat loan dikembalikan, `lending-service` memanggil endpoint `PATCH /api/books/{id}/release`
- Tujuannya untuk menambah kembali `stock_available`

### Alur Integrasi

Alur create loan:

1. request masuk ke `lending-service`
2. `lending-service` validasi member dan status loan aktif
3. `lending-service` memanggil `catalog-service` untuk reserve stok
4. jika stok tersedia, data loan disimpan

Alur return loan:

1. request return masuk ke `lending-service`
2. status loan diubah menjadi `RETURNED`
3. `lending-service` memanggil `catalog-service` untuk release stok

### Kelas Yang Terlibat

- Client integrasi HTTP: [CatalogClient](file:///d:/test-mandiri/lending-service/src/main/java/com/mandiri/lending/integration/catalog/CatalogClient.java)
- Logic pinjam/kembali: [LoanService](file:///d:/test-mandiri/lending-service/src/main/java/com/mandiri/lending/domain/service/LoanService.java)
- Endpoint buku: [BookController](file:///d:/test-mandiri/catalog-service/src/main/java/com/mandiri/catalog/api/controller/BookController.java)

## Catatan Implementasi

- JSON response/request menggunakan `snake_case`
- `catalog-service` fokus pada buku dan stok
- `lending-service` fokus pada member, loan, dan analytics
- Native SQL query digunakan pada fitur analytics summary peminjaman member
- Java Stream digunakan pada proses mapping list dan transformasi response
