drop index if exists idx_users_enabled;
drop index if exists idx_users_role;
drop index if exists idx_users_status;
drop index if exists idx_file_metadata_checksum;
drop index if exists idx_file_metadata_owner_status_created_at;

create index if not exists idx_file_metadata_owner_created_at
    on file_metadata (owner_user_id, created_at, id);

create index if not exists idx_file_metadata_status_stored_filename
    on file_metadata (status, stored_filename);
