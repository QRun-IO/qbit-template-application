# qbit-platform-content Technical Specification

## 1. Overview

The `qbit-platform-content` QBit is a Content Management System (CMS) / blog system designed to manage structured content. It supports a draft-to-publish workflow and integrates with MinIO for asset storage.

## 2. Data Models

### 2.1. Tables (prefix: `cms_`)

1.  **`site`**:
    *   `id` (UUID, PK)
    *   `name` (VARCHAR, UNIQUE, NOT NULL)
    *   `domain` (VARCHAR, UNIQUE, NULLABLE)
    *   `default_language` (VARCHAR, NOT NULL)
    *   `created_at`, `updated_at`

2.  **`page`**:
    *   `id` (UUID, PK)
    *   `site_id` (UUID, FK to `site.id`)
    *   `slug` (VARCHAR, NOT NULL)
    *   `title` (VARCHAR, NOT NULL)
    *   `content` (TEXT, NULLABLE)
    *   `seo_description` (VARCHAR, NULLABLE)
    *   `publication_state` (VARCHAR, NOT NULL, DEFAULT 'draft') -- 'draft', 'published', 'archived'
    *   `published_at` (TIMESTAMP, NULLABLE)
    *   `created_at`, `updated_at`
    *   *Indexes*: `(site_id, slug)`, `publication_state`

3.  **`blog_post`**:
    *   `id` (UUID, PK)
    *   `site_id` (UUID, FK to `site.id`)
    *   `slug` (VARCHAR, NOT NULL)
    *   `title` (VARCHAR, NOT NULL)
    *   `excerpt` (TEXT, NULLABLE)
    *   `content` (TEXT, NULLABLE)
    *   `featured_image_asset_id` (UUID, FK to `asset.id`, NULLABLE)
    *   `publication_state` (VARCHAR, NOT NULL, DEFAULT 'draft') -- 'draft', 'published', 'archived'
    *   `published_at` (TIMESTAMP, NULLABLE)
    *   `created_at`, `updated_at`
    *   *Indexes*: `(site_id, slug)`, `publication_state`

4.  **`author`**:
    *   `id` (UUID, PK)
    *   `site_id` (UUID, FK to `site.id`)
    *   `name` (VARCHAR, NOT NULL)
    *   `bio` (TEXT, NULLABLE)
    *   `avatar_asset_id` (UUID, FK to `asset.id`, NULLABLE)
    *   `created_at`, `updated_at`

5.  **`tag`**:
    *   `id` (UUID, PK)
    *   `site_id` (UUID, FK to `site.id`)
    *   `name` (VARCHAR, NOT NULL)
    *   `slug` (VARCHAR, NOT NULL)
    *   *Indexes*: `(site_id, slug)`

6.  **`asset`**:
    *   `id` (UUID, PK)
    *   `site_id` (UUID, FK to `site.id`)
    *   `storage_key` (VARCHAR, NOT NULL) -- Key within MinIO bucket
    *   `name` (VARCHAR, NOT NULL)
    *   `mime_type` (VARCHAR, NOT NULL)
    *   `size_bytes` (BIGINT, NOT NULL)
    *   `url` (VARCHAR, NOT NULL) -- Generated URL for direct access
    *   `created_at`

7.  **`navigation`**:
    *   `id` (UUID, PK)
    *   `site_id` (UUID, FK to `site.id`)
    *   `name` (VARCHAR, NOT NULL) -- e.g., "Main Navigation", "Footer Links"
    *   `created_at`

8.  **`navigation_item`**:
    *   `id` (UUID, PK)
    *   `navigation_id` (UUID, FK to `navigation.id`)
    *   `parent_id` (UUID, FK to `navigation_item.id`, NULLABLE) -- For tree structure
    *   `label` (VARCHAR, NOT NULL)
    *   `type` (VARCHAR, NOT NULL) -- e.g., 'page', 'blog_post', 'link'
    *   `target_id` (UUID, NULLABLE) -- FK to `page.id` or `blog_post.id`
    *   `custom_link` (VARCHAR, NULLABLE) -- URL for 'link' type
    *   `order` (INTEGER, NOT NULL)
    *   `created_at`

9.  **`redirect`**:
    *   `id` (UUID, PK)
    *   `site_id` (UUID, FK to `site.id`)
    *   `from_path` (VARCHAR, NOT NULL)
    *   `to_path` (VARCHAR, NOT NULL)
    *   `type` (VARCHAR, NOT NULL, DEFAULT '301') -- e.g., 301, 302
    *   `created_at`
    *   *Indexes*: `(site_id, from_path)`

10. **`blog_post_tag`**: (Join table)
    *   `blog_post_id` (UUID, FK to `blog_post.id`)
    *   `tag_id` (UUID, FK to `tag.id`)
    *   PRIMARY KEY (`blog_post_id`, `tag_id`)

## 3. Gen-2 Producer Implementation Patterns

-   **DDL Management:** Distribution owns 100% of DDL via Liquibase. This QBit ships only metadata.
-   **Content Workflow:** Support for `draft` -> `published` -> `archived` states.
-   **Asset Storage:** All uploaded assets (images, documents) will be stored in MinIO.
    -   The `asset` table will store metadata about the assets, including the `storage_key` (object key in MinIO) and a generated `url` for accessing the asset.
    -   Service integrates with MinIO client for uploads and retrieval.
-   **API Endpoints:** Provide RESTful API endpoints for CRUD operations on content types (pages, posts, etc.), asset management, and navigation management.

## 4. Customizer Slots

-   **`content_pre_save_hook`**: (Example hook) Called before saving any content item (page, post). Can be used for validation, preparing content, or setting default values.
-   **`asset_upload_hook`**: Called after an asset is uploaded to MinIO. Can be used to populate the `asset` table.
-   **`publication_hook`**: Triggered when an item's `publication_state` is changed to 'published'. Can be used to trigger downstream actions (e.g., cache invalidation, search indexing).

## 5. Security Lock Configurations

-   **Publication State Locking:**
    -   A record lock mechanism will be implemented to prevent concurrent edits on content items that are in a published state and are being actively modified.
    -   The `publicationState=published` constraint should be strictly enforced for public-facing retrieval. Any modification attempts on published content should create a new draft or return an error.
-   **Asset Access Control:** Access to assets in MinIO should be governed by RBAC. If an asset is associated with public content, it should be publicly accessible. Private content assets require authentication and authorization.

## 6. Integration Points

-   **MinIO:** For object storage of assets.
-   **Distribution Layer:** Manages Liquibase for DDL.
-   **RBAC System:** Keycloak roles and `qbit-user-role-permissions` for access control to content management functions.
-   **Search Indexing Service:** (External) To index published content for efficient searching.
-   **CDN:** For optimized delivery of assets and published content.

## 7. CRM Rehabilitation Strategy - N/A for this QBit

This QBit is new and does not directly integrate with CRM rehabilitation. Its content may reference contacts or companies managed by the CRM, but synchronization is not its primary concern.
