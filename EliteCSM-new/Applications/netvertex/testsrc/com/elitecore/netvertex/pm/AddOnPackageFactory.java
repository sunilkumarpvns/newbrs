package com.elitecore.netvertex.pm;


public class AddOnPackageFactory {/*

	private AddOnPackageFactory() {
	}

	public static AddOnPackageBuilder createExclusiveAddOn(String name) {
		AddOnPackageBuilder addOnPackageFactory = new AddOnPackageBuilder(name, true);
		addOnPackageFactory.isExclusive = true;

		return addOnPackageFactory;
	}

	public static AddOnPackageBuilder createNonExclusiveAddOn(String name) {
		return new AddOnPackageBuilder(name, false);
	}

	public static class AddOnPackageBuilder {
		private boolean isExclusive;

		private QoSProfile hsqLevelQosProfile;
		private List<QoSProfile> qoSProfiles;
		private IPCANQoS upperBound;
		private IPCANQoS lowerBound;
		private IPCANQoS equalSessionQoS;
		private String name;

		public AddOnPackageBuilder(String name, boolean isExclusive) {
			this.name = name;
			this.isExclusive = isExclusive;
		}

		public AddOn build() {

			IPCANQoS sessionQoS = IPCanQoSFactory.create().hasHigherQoSThan(lowerBound)
					.hasLowerQoSThan(upperBound)
					.hasEqualQoSThan(equalSessionQoS)
					.build();

			QoSProfile qoSProfile = QosProfileFactory.createQosProfileHasQoSEqualTo(sessionQoS);

			AddOnBuilder addOnBuilder = new AddOn.AddOnBuilder(UUID.randomUUID().toString(), name).
					addQoSProfile(qoSProfile).
					wihtValidityPeriod(10).
					wihtValidityPeriodUnit(ValidityPeriodUnit.DAY).
					withPriority(1);

			if (isExclusive) {
				addOnBuilder.exclusiveAddOn();
			}

			return addOnBuilder.build();

		}

		public AddOnPackageBuilder hasLowerQosThan(Package basePackage) {

			IPCANQoS higestSessionQoS = AddOnPackageFactory.findHigestQoS(basePackage);

			if (higestSessionQoS == null) {
				throw new RuntimeException("Can not create addOn package from package:" + basePackage.getName()
						+ ". Reason: All quota profile has reject action");
			}

			this.upperBound = higestSessionQoS;

			return this;
		}

		public AddOnPackageBuilder hasHigherQosThan(Package basePackage) {

			IPCANQoS higestSessionQoS = findHigestQoS(basePackage);

			if (higestSessionQoS == null) {
				throw new RuntimeException("Can not create addOn package from package:" + basePackage.getName()
						+ ". Reason: All quota profile has reject action");
			}

			this.lowerBound = higestSessionQoS;
			return this;
		}

		public AddOnPackageBuilder hasEqualQosThan(Package basePackage) {

			IPCANQoS higestSessionQoS = findHigestQoS(basePackage);

			if (higestSessionQoS == null) {
				throw new RuntimeException("Can not create addOn package from package:" + basePackage.getName()
						+ ". Reason: All quota profile has reject action");
			}

			this.equalSessionQoS = higestSessionQoS;
			return this;

		}

	}

	public static IPCANQoS findHigestQoS(Package basePackage) {

		IPCANQoS higestSessionQoS = null;
		for (QoSProfile qosProfile : basePackage.getQoSProfiles()) {
			QoSProfileDetail qoSProfileDetail = qosProfile.getHsqLevelQoSDetail();

			if (qoSProfileDetail.getAction() == QoSProfileAction.REJECT) {
				continue;
			}

			IPCANQoS ipcanQoS = qoSProfileDetail.getSessionQoS();

			if (higestSessionQoS == null) {
				higestSessionQoS = ipcanQoS;
			} else {
				if (higestSessionQoS.compareTo(ipcanQoS) < 0) {
					higestSessionQoS = ipcanQoS;
				}
			}

		}

		if (higestSessionQoS == null) {
			throw new RuntimeException(basePackage.getName() + " does not contain any QoS");
		}

		return higestSessionQoS;

	}

*/}
