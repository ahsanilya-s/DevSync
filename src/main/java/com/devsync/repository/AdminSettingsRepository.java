package com.devsync.repository;

import com.devsync.model.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminSettingsRepository extends JpaRepository<AdminSettings, Long> {
    Optional<AdminSettings> findBySettingKey(String settingKey);
    List<AdminSettings> findByCategory(String category);
}