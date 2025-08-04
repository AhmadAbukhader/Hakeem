package com.system.hakeem.Repository.UserManagement;

import com.system.hakeem.Model.UserManagement.Role;
import com.system.hakeem.Model.UserManagement.Type;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
 Role findById(int id );
 Role findByRole(Type role);
}
