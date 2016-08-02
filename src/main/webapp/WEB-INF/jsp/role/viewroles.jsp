<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script	src="<%=request.getContextPath()%>/static/positivepay/js/role.js" type="text/javascript"></script>

<div>
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Roles</div>
			</div>
			<div class="panel-body">
				<form:form method="post" enctype="form-data" modelAttribute="role" class="form-inline" action="operation" role="form" id="viewroleform"> 
					<div id="info" class="alert alert-success alert-dismissable" hidden="true"></div>
					<div id="error" class="alert alert-danger alert-dismissable" hidden="true">
						<button type="button" class="close" data-dismiss="alert">&times;</button>				 	
					</div>
					<div class="form-group">
						<div>
							<button id="newRole" name="newRole" value="newRole" type="button" class="btn button">
								New Role
							</button>
						</div>
					</div>				
					<br><br>
					<table class="table table-striped table-bordered">					
						<thead>
							<tr>
						    	<th class="col-sm-8">Role Name</th>
						    	<th class="col-sm-1" style="text-align:center">Edit</th>
						    	<th class="col-sm-1" style="text-align:center">Delete</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var='role' items="${roleList}">
									<tr id ="row-${role.id}">
								    	<td align="left">${role.label}</td>
								    	<td align="center">									    	
			                                <a href="#" href1="<%=request.getContextPath()%>/role/editrole?id=${role.id}" class="edit-link"> 
				                             	<div class="pp-sprite pp-sprite-edit"></div>
			                             	</a>				                         	
				                        </td>
										<td align="center">
			                         		<a href="#" href1="<%=request.getContextPath()%>/role/deleterole?id=${role.id}" class="confirm-link">
			                         			<div class="pp-sprite pp-sprite-delete"></div>
			                         		</a>			                         		
			                        	</td>
									</tr>
						   </c:forEach>
						</tbody>
					</table>				
				</form:form>
			</div>
		</div>
	</div>
</div>

<!-- Start Delete Role Modal -->
<div class="modal fade bs-modal-xs" id="deleteConfirmModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-xs">
		<div class="modal-content">
			<div class="modal-header">
				<label class="modal-title">Delete Role</label>
				<div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#deleteConfirmModal" aria-hidden="true"></button>
				</div>
			</div>
   			<div class="modal-body">
				Do you want to delete this role?
   			</div>
   			<div class="modal-footer">
      			<button type="button" class="btn button" data-dismiss="modal">No</button>
      			<a href="#" data-id="${deleteConfirmModal.data-id}" class="btn button del-link" role="button">Yes</a>
			</div>
		</div>
	</div>
</div>
<!-- End Delete Role Modal  -->

<!-- Start Show Add Role Modal -->
<div class="modal" id="newRoleModalShow" tabindex="-1" role="dialog" aria-labelledby="newRoleModalLabel" aria-hidden="true">
	<div class="modal-dialog" style="width:700px;">
		<div class="modal-content">
			<div class="modal-header">
				<label class="modal-title">Role</label>
				<div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#newRoleModalShow" aria-hidden="true"></button>
				</div>
			</div>
			<div class="modal-body">
				<div id="info" class="alert alert-success alert-dismissable" hidden="true"></div>
				<div id="error" class="alert alert-danger alert-dismissable" hidden="true">
					<button type="button" class="close" data-dismiss="alert">&times;</button>				 	
				</div>
				<form:form id="saveRoleForm" method="POST" modelAttribute="role" role="form" class="form-horizontal" action="saverole">
					<div class="form-group">	
						<input type="text" class="col-sm-4 label-large" id="roleName" placeholder="Role Name" name="roleName" />
						<input type="hidden" class="col-sm-2" id="edit" name="edit" />
						<input type="hidden" class="col-sm-2" id="roleId" name="roleId" />
						<label for="roleLabel" class="col-sm-1 control-label"></label>		
						<input type="text" class="col-sm-3 label-large" id="roleLabel" placeholder="Role Label" name="roleLabel" />	
						<div style="float: right">
							<input type="checkbox" id="selectAll" name="selectAll" value=""/>							
							<label for="selectAll" class="control-label">Select All</label>&nbsp;&nbsp;							
							<button id="saveNewRole" name="saveNewRole" value="saveNewRole" type="button" class="btn button saveNewRole">Save</button>
						</div>
					</div>
					
					<div class="form-group">
						<label for="items" class="label-large">Items</label>				
						<hr>
					</div>					
					<div id="items" class="form-group"></div>					
					
					<div class="form-group">
						<br>
						<label for="manualEntry" class="label-large">Manual Entry</label>					
						<hr>
					</div>					
					<div id="manualEntry" class="form-group"></div>
					
					<div class="form-group">
						<br>
						<label for="userRoleManagement" class="label-large">User/Role Management</label>				
						<hr>
					</div>										
					<div id="userRoleManagement" class="form-group"></div>
					<div class="form-group">
						<br>
						<label for="payments" class="label-large">Payments</label>				
						<hr>
					</div>			
					<div id="payments" class="form-group"></div>
					<div class="form-group">
						<br>
						<label for="otherPermissions" class="label-large">Other Permissions</label>				
						<hr>
					</div>			
					<div id="otherPermissions" class="form-group"></div>
				</form:form>						
			</div>	
			<div class="modal-footer">
				<button id="saveNewRole" name="saveNewRole" value="saveNewRole" type="button" class="btn button saveNewRole">Save</button>
	      	</div>			
		</div>
	</div>
</div>
<!-- End  Show Add Role Modal -->


