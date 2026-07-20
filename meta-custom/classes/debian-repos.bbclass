DEBIAN_REPOS ??= "trixie trixie-security trixie-backports bullseye"

# Repository configuration
python do_setup_debian_repos() {
    import os
    
    workdir = d.getVar('WORKDIR')
    staging_dir = d.getVar('IMAGE_ROOTFS')
    
    # Create apt sources configuration
    sources_dir = os.path.join(staging_dir, 'etc', 'apt', 'sources.list.d')
    bb.utils.mkdirhier(sources_dir)

    conf_dir = workdir + "../conf/apt-config/sources-list"
    os.popen("cp " + conf_dir + "/*" + " " + sources_dir)

}

python do_setup_package_priorities() {
    import os
    
    staging_dir = d.getVar('IMAGE_ROOTFS')
    preferences_dir = os.path.join(staging_dir, 'etc', 'apt', 'preferences.d')
    bb.utils.mkdirhier(preferences_dir)
    
    conf_dir = workdir + "../conf/apt-config/package-preferences"
    os.popen("cp " + conf_dir + "/*" + " " + preferences_dir)
}

addtask setup_debian_repos after do_configure before do_compile
addtask setup_package_priorities after do_setup_debian_repos before do_compile

