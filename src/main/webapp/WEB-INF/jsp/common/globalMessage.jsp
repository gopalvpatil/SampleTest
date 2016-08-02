<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script	src="<%=request.getContextPath()%>/static/positivepay/js/globalMessage.js" type="text/javascript"></script>


<!--<div class="row" id="maintenanceMessage" style="display: none;">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">System Maintenance Message</div>
			</div>

			<div class="panel-body">
				<label id="labelMaintenanceMessage" class="maintenance-message-text"></label>
			</div>
		</div>
	</div>
</div>-->

<c:if test="${not empty emulation}">
	<div class="row" id="emulate">
		<div class="positivepay-column-spacer">
			<div class="panel panel-primary" style="background-color: #ECECEC;">
				<div id="fileMappingNotSet" class="panel-body">
					<div class="pp-sprite pp-sprite-caution" style="float: left;"></div>
					<div style="padding-left: 40px; padding-top: 3px;">
						<strong class="emulation-text">You are in emulation mode,
							no changes to the system will be made while you are in this mode.
							<a href="<%=request.getContextPath()%>/user/emulation/exit">Exit
								Emulation</a>
						</strong>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>